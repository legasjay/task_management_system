package com.olegandreevich.tms.repositories;

import com.olegandreevich.tms.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByAuthor_Id(Long authorId);

    List<Task> findByAssignee_Id(Long assigneeId);

}
