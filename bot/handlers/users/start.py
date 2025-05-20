from aiogram import types
from aiogram.dispatcher.filters.builtin import CommandStart
from aiogram.types import Message, InlineKeyboardMarkup, InlineKeyboardButton

from loader import dp, db, bot
from data.config import ADMINS


@dp.message_handler(CommandStart())
async def bot_start(message: types.Message):
    try:
        # Print debug info
        print(f"Attempting to add user: {message.from_user.id}, "
              f"@{message.from_user.username}, {message.from_user.full_name}")

        # Add/update user in database
        user = await db.add_user(
            telegram_id=message.from_user.id,
            username=message.from_user.username,
            full_name=message.from_user.full_name
        )

        if user:
            print(f"User in database: {user}")
            await send_welcome_message(message, user)
            await notify_admins(message)
        else:
            # Check if user exists despite add failing
            if await db.user_exists(message.from_user.id):
                await message.answer("Qaytganingiz bilan! 😊")
            else:
                await message.answer("Ro'yxatdan o'tishda xatolik. Iltimos, /start ni qayta bosing.")

    except Exception as e:
        print(f"Error in bot_start: {e}")
        await message.answer("Botda texnik nosozlik. Iltimos, keyinroq urinib ko'ring.")

async def send_welcome_message(message: Message, user):
    text = (
        "🤝 *XUSH KELIBSIZ!*\n\n"
        "Bu bot orqali siz virtual tangalar to'plash va ularni real sovg'alar uchun almashtirishingiz mumkin!\n\n"
        "🔹 Har kungilik topshiriqlarni bajaring\n"
        "🔹 Do'stlaringizni taklif qiling\n"
        "🔹 Sovg'alarni qo'lga kiriting\n\n"
        "Boshlash uchun /help buyrug'ini yuboring yoki quyidagi menyulardan foydalaning."
    )

    keyboard = InlineKeyboardMarkup(
        inline_keyboard=[
            [InlineKeyboardButton(text="📚 Qo'llanma", callback_data="help")],
            [InlineKeyboardButton(text="🏆 Mening balansim", callback_data="balance")],
            [InlineKeyboardButton(text="🎁 Sovg'alar", callback_data="gifts")],
            [InlineKeyboardButton(
                text="🌐 Vebsayt",
                url=f"https://d8d5-213-230-108-224.ngrok-free.app/{str(user['id'])}"
            )]
        ]
    )

    await message.answer(text, parse_mode="Markdown", reply_markup=keyboard)


async def notify_admins(message: Message):
    if not ADMINS:
        return

    text = (
        f"⚠️ Yangi foydalanuvchi!\n\n"
        f"🆔 ID: {message.from_user.id}\n"
        f"👤 Ism: {message.from_user.full_name}\n"
        f"📛 Username: @{message.from_user.username or 'yoq'}\n"
        f"📅 Ro'yxatdan o'tdi: {message.date}"
    )

    for admin in ADMINS:
        try:
            await bot.send_message(admin, text)
        except Exception as e:
            print(f"Failed to notify admin {admin}: {e}")