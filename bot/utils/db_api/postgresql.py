from typing import Union
import asyncpg
from asyncpg import Connection
from asyncpg.pool import Pool
from data import config

class Database:
    def __init__(self):
        self.pool: Union[Pool, None] = None

    async def create(self):
        self.pool = await asyncpg.create_pool(
            user=config.DB_USER,
            password=config.DB_PASS,
            host=config.DB_HOST,
            database=config.DB_NAME,
            port=config.DB_PORT,
        )
        await self.execute("CREATE EXTENSION IF NOT EXISTS pgcrypto;", execute=True)
        await self.create_table_users()

    async def execute(self, command, *args,
                      fetch: bool = False,
                      fetchval: bool = False,
                      fetchrow: bool = False,
                      execute: bool = False):
        async with self.pool.acquire() as connection:
            connection: Connection
            async with connection.transaction():
                if fetch:
                    return await connection.fetch(command, *args)
                elif fetchval:
                    return await connection.fetchval(command, *args)
                elif fetchrow:
                    return await connection.fetchrow(command, *args)
                elif execute:
                    return await connection.execute(command, *args)

    async def create_table_users(self):
        sql = """
        CREATE TABLE IF NOT EXISTS telegram_users (
            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
            telegram_id BIGINT UNIQUE NOT NULL,
            username TEXT,
            full_name TEXT,
            created_at TIMESTAMP DEFAULT (now() AT TIME ZONE 'Asia/Tashkent'),
            is_first_time BOOLEAN DEFAULT TRUE,
            available_coin INT DEFAULT 0,
            level INT DEFAULT 1,
            energy INT DEFAULT 1000
        );
        """
        await self.execute(sql, execute=True)
        print("Users table created/verified")

    async def add_user(self, telegram_id: int, username: str, full_name: str):
        sql = """
        INSERT INTO telegram_users (telegram_id, username, full_name)
        VALUES ($1, $2, $3)
        ON CONFLICT (telegram_id) DO UPDATE
        SET username = EXCLUDED.username,
            full_name = EXCLUDED.full_name
        RETURNING *;
        """
        try:
            result = await self.execute(
                sql,
                telegram_id,
                username or "",
                full_name or "",
                fetchrow=True
            )
            print(f"User added/updated: {result}")
            return result
        except Exception as e:
            print(f"[DB ERROR] Failed to add user: {e}")
            return None

    async def select_user(self, telegram_id: int):
        sql = "SELECT * FROM telegram_users WHERE telegram_id = $1;"
        return await self.execute(sql, telegram_id, fetchrow=True)

    async def user_exists(self, telegram_id: int):
        sql = "SELECT EXISTS(SELECT 1 FROM telegram_users WHERE telegram_id = $1);"
        return await self.execute(sql, telegram_id, fetchval=True)

    async def test_connection(self):
        try:
            return await self.execute("SELECT 1", fetchval=True) == 1
        except Exception as e:
            print(f"Connection test failed: {e}")
            return False