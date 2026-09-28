package project.librotrack.controller;

import project.librotrack.model.IssueRecord;
import project.librotrack.service.IssueRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/issues")
public class IssueRecordController {

    private final IssueRecordService issueRecordService;

    public IssueRecordController(IssueRecordService issueRecordService) {
        this.issueRecordService = issueRecordService;
    }

    // Get all issue records
    @GetMapping
    public List<IssueRecord> getAllIssues() {
        return issueRecordService.getAllIssues();
    }

    // Get issue record by ID
    @GetMapping("/{id}")
    public ResponseEntity<IssueRecord> getIssueById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                issueRecordService.getIssueById(id)
        );
    }

    // Issue a book
    @PostMapping("/issue")
    public ResponseEntity<IssueRecord> issueBook(
            @RequestParam Long bookId,
            @RequestParam Long studentId) {

        return ResponseEntity.ok(
                issueRecordService.issueBook(bookId, studentId)
        );
    }

    // Return a book
    @PutMapping("/return/{id}")
    public ResponseEntity<IssueRecord> returnBook(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                issueRecordService.returnBook(id)
        );
    }

    // Get currently issued books for a student
    @GetMapping("/student/{studentId}")
    public List<IssueRecord> getIssuedBooksByStudent(
            @PathVariable Long studentId) {

        return issueRecordService
                .getIssuedBooksByStudent(studentId);
    }
}