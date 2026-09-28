package project.librotrack.repository;

import project.librotrack.model.IssueRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssueRecordRepository extends JpaRepository<IssueRecord, Long> {

    List<IssueRecord> findByStudentIdAndStatus(
            Long studentId,
            String status
    );
}