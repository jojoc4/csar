package ch.hearc.csar.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import ch.hearc.csar.model.Loan;

public class GeneratePDF {
	public static ByteArrayInputStream loanLabel(int loanId) {
		Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        try {
        	PdfPTable table = new PdfPTable(1);
        	
        	PdfPCell cell;
        	cell = new PdfPCell(new Phrase("csar loan id:"+loanId));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        	
        	
        	PdfWriter.getInstance(document, out);
        	document.open();
        	document.add(table);
        	document.close();
        } catch (DocumentException ex) {

            System.out.println(ex);
        }
        
        return new ByteArrayInputStream(out.toByteArray());
	}
}
