package com.example.backend.Repository;

import com.example.backend.Entity.DailyControlSubFolder;
import com.example.backend.Entity.DailyControlSubFolderContent;
import com.example.backend.Entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DailyControlSubFolderRepo extends JpaRepository<DailyControlSubFolder,Integer> {
    List<DailyControlSubFolder> findByDailyControlId(Integer dailyControlId);

    List<DailyControlSubFolder> findByStaff(Staff staff);

    @Query(value = "select * from daily_control_sub_folder where staff_id=:staffId", nativeQuery = true)
    List<DailyControlSubFolder> findByStaffId(Integer staffId);

}
