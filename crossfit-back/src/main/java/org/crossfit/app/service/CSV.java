package org.crossfit.app.service;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.enumeration.Title;
import org.crossfit.app.exception.CSVParseException;
import org.joda.time.LocalDate;

import com.opencsv.CSVReader;

public class CSV<T> {

	private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");

	private boolean hasHeader;
	
	private List<String> columns = new ArrayList<>();
	private Map<String, Function<T, Object>> getMapper = new HashMap<>();
	private Map<String, BiConsumer<T, String>> setMapper = new HashMap<>();

	private final Supplier<T> instanceSupplier;

	public CSV(boolean hasHeader, Supplier<T> instanceSupplier ) {
		this.hasHeader = hasHeader;
		this.instanceSupplier = instanceSupplier;
	}

	public String format(Iterable<T> datas) {
		StringBuilder sb = new StringBuilder();
		if (hasHeader)
			sb.append(columnLine()).append("\n");
		
		for (T data : datas) {
			sb.append(columns.stream().map(col->getMapper.get(col).apply(data)).map(o->o==null ? "" : o.toString()).collect(Collectors.joining(";", "\"", "\""))).append("\n");
		}		
		
		return sb.toString();
	}

	private String columnLine() {
		return columns.stream().collect(Collectors.joining(";", "[", "]"));
	}
	
	public Collection<T> parse(Reader reader) throws IOException, CSVParseException{

		try(CSVReader csvreader = new CSVReader(reader)){

			List<T> elements = new ArrayList<>();
			
			if (hasHeader && !columnLine().equals(csvreader.readNext())) {
				throw new CSVParseException("La première ligne n'est pas une entête: " + columnLine());
			}

			int index = hasHeader ? 1 : 0;
			String[] cells = csvreader.readNext();
			index ++;
			while (cells != null) {

				if (cells.length != columns.size())
					throw new CSVParseException("La ligne " + index + " ne contient que " + cells.length + " colonnes alors qu'elle devrait en contenir " + columns.size());
				

				T e = instanceSupplier.get();
				for (int i = 0; i < cells.length; i++) {
					String cell = cells[i];
					this.setMapper.get(columns.get(i)).accept(e, cell);
				}
				elements.add(e);			
				
				cells = csvreader.readNext();
				index ++;
			}
			
			
			return elements;
		}
		
		
	}
	

	public void addMapping(String columnName, Function<T, Object> get, BiConsumer<T, String> set) {
		this.columns.add(columnName);
		this.getMapper.put(columnName, get);
		this.setMapper.put(columnName, set);
	}
	
	public static final CSV<Member> members(){		
		return csvMember;
	}
	
	private static final CSV<Member> csvMember = new CSV<>(true, Member::new);
	static {

		csvMember.addMapping("Id", Member::getId, (m, val)->m.setId(Long.valueOf(val)));
		csvMember.addMapping("MemberNumber", Member::getNumber, Member::setNumber);
		csvMember.addMapping("Title", Member::getTitle, (m, val)->m.setTitle(Title.valueOf(val)));
		csvMember.addMapping("FirstName", Member::getFirstName, Member::setFirstName);
		csvMember.addMapping("LastName", Member::getLastName, Member::setLastName);
		csvMember.addMapping("Email", Member::getLogin, Member::setLogin);
		csvMember.addMapping("CardNumber", Member::getCardUuid, Member::setCardUuid);
		csvMember.addMapping("hasCertMed", Member::hasGivenMedicalCertificate, (m, val)->m.setGivenMedicalCertificate(Boolean.valueOf(val)));
		csvMember.addMapping("certMedDate",
				m->
					m.getMedicalCertificateDate()==null ? "" : SDF.format(m.getMedicalCertificateDate().toDate()),
				(m, val) -> {
						if (val!=null) {
							try {
								m.setMedicalCertificateDate(new LocalDate(SDF.parse(val.toString())));
							} catch (ParseException e) {
							}
						}
				});
		csvMember.addMapping("Telephon", Member::getTelephonNumber, Member::setTelephonNumber);
		csvMember.addMapping("Address", Member::getAddress, Member::setAddress);
		csvMember.addMapping("ZipCode", Member::getZipCode, Member::setZipCode);
		csvMember.addMapping("City", Member::getCity, Member::setCity);
	}
}
