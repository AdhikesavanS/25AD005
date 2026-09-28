package project.librotrack.service;

import project.librotrack.model.Book;
import project.librotrack.model.Student;
import project.librotrack.model.IssueRecord;
import project.librotrack.repository.BookRepository;
import project.librotrack.repository.StudentRepository;
import project.librotrack.repository.IssueRecordRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class IssueRecordService {

    private final IssueRecordRepository issueRecordRepository;
    private final BookRepository bookRepository;
    private final StudentRepository studentRepository;

    private static final double FINE_PER_DAY = 5.0;

    public IssueRecordService(
            IssueRecordRepository issueRecordRepository,
            BookRepository bookRepository,
            StudentRepository studentRepository) {

        this.issueRecordRepository = issueRecordRepository;
        this.bookRepository = bookRepository;
        this.studentRepository = studentRepository;
    }

    // Get all issue records
    public List<IssueRecord> getAllIssues() {
        return issueRecordRepository.findAll();
    }

    // Get issue record by ID
    public IssueRecord getIssueById(Long id) {

        return issueRecordRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Issue record not found"));
    }

    // Issue a book
    public IssueRecord issueBook(Long bookId, Long studentId) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() ->
                        new RuntimeException("Book not found"));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new RuntimeException("Student not found"));

        // Business Rule: No copies available
        if (book.getAvailableCopies() <= 0) {
            throw new RuntimeException(
                    "Book cannot be issued. No copies available."
            );
        }

        IssueRecord issueRecord = new IssueRecord();

        issueRecord.setBook(book);
        issueRecord.setStudent(student);

        LocalDate issueDate = LocalDate.now();

        issueRecord.setIssueDate(issueDate);

        // Due date = 14 days
        issueRecord.setDueDate(
                issueDate.plusDays(14)
        );

        issueRecord.setStatus("ISSUED");
        issueRecord.setFine(0);

        // Reduce available copies
        book.setAvailableCopies(
                book.getAvailableCopies() - 1
        );

        bookRepository.save(book);

        return issueRecordRepository.save(issueRecord);
    }

    // Return a book
    public IssueRecord returnBook(Long id) {

        IssueRecord issueRecord = getIssueById(id);

        if ("RETURNED".equals(issueRecord.getStatus())) {
            throw new RuntimeException(
                    "Book has already been returned."
            );
        }

        LocalDate returnDate = LocalDate.now();

        issueRecord.setReturnDate(returnDate);

        // Calculate late days
        long lateDays = ChronoUnit.DAYS.between(
                issueRecord.getDueDate(),
                returnDate
        );

        double fine = 0;

        if (lateDays > 0) {
            fine = lateDays * FINE_PER_DAY;
        }

        issueRecord.setFine(fine);
        issueRecord.setStatus("RETURNED");

        // Increase available copies
        Book book = issueRecord.getBook();

        book.setAvailableCopies(
                book.getAvailableCopies() + 1
        );

        bookRepository.save(book);

        return issueRecordRepository.save(issueRecord);
    }

    // Get currently issued books for a student
    public List<IssueRecord> getIssuedBooksByStudent(
            Long studentId) {

        return issueRecordRepository
                .findByStudentIdAndStatus(
                        studentId,
                        "ISSUED"
                );
    }
}