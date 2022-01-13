package ch.hearc.csar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ch.hearc.csar.model.Loan;
import ch.hearc.csar.model.Object;
import ch.hearc.csar.model.User;

@Repository("LoanRepository")
public interface LoanRepository extends JpaRepository<Loan, Integer> {
	
	Loan findByObjectAndBorrower(Object object, User borrower);
	
	List<Loan> findByBorrower(User borrower);
	
	List<Loan> findByObject(Object object); 
	
	List<Loan> findByObjectOrderByDate(Object object);

	Loan findByObjectOrderByDateDesc(Object object);

}
