package com.example.backend.Repository;

import com.example.backend.Entity.DailyControlSubFolderContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DailyControlSubFolderContentRepo extends JpaRepository<DailyControlSubFolderContent,Integer> {

    @Query(value = "SELECT * FROM daily_control_sub_folder_content WHERE daily_control_sub_folder_id = :subfolderId ORDER BY created_at DESC", nativeQuery = true)

    List<DailyControlSubFolderContent> findBySubfolderId( Integer subfolderId);

}
