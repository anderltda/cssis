package br.com.cssis.foundation.util;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import br.com.cssis.foundation.BasicObject;
import br.com.cssis.foundation.ExceptionConstants;
import br.com.cssis.foundation.ExceptionHandler;
import br.com.cssis.foundation.service.ServiceException;

public class DateTimeHelper extends BasicObject {
	private static final long serialVersionUID = 1L;
	private static String defaultAmericanFormat = "yyyyMMdd";
	private static String defaultAmericanTimeFormat = "HHmmss";
	private static String defaultAmericanDateTimeFormat = defaultAmericanFormat + defaultAmericanTimeFormat;
	private static String defaultDateFormat = "dd/MM/yyyy";
	private static String defaultTimeFormat = "HH:mm";
	private static String defaultDateTimeFormat = defaultDateFormat + " " + defaultTimeFormat;
	private static String defaultFullDateTimeFormat = defaultDateFormat + " " + defaultTimeFormat + ":ss";
	private static String defaultTimeMask = "([0-1][0-9]|[2][0-3]):([0-5][0-9])";

	private static final long DIFF_IN_DAYS_FACTOR = 24 * 60 * 60 * 1000;
	private static final double DIFF_IN_MONTHS_FACTOR = 30.0 * 24.0 * 60.0 * 60.0 * 1000.0; 
	private static final long DIFF_IN_HOURS_FACTOR = 1;

	private static final List<Integer> listaNumerosMeses = new ArrayList<Integer>();
	static {
		for (int i = 1; i <= 12; i++) {
			listaNumerosMeses.add(i);
		}
	}

	public static List<Integer> getListaNumerosMeses() {
		return listaNumerosMeses;
	}

	public static List<Integer> getListaProximosAnos(int qtde) {
		List<Integer> anos = new ArrayList<Integer>();
		int anoAtual = getYear(new Date());
		for (int i = anoAtual; i < anoAtual + qtde; i++) {
			anos.add(i);
		}
		return anos;
	}

	public static Date getDate(String value) throws ParseException {
		return ThreadSafeDateParser.parse(value, defaultDateFormat);
	}

	public static Map<String, String> getMonths(){
		Map<String, String> monthsMap = new LinkedHashMap<String, String>();
		String[] months = new DateFormatSymbols().getMonths();
	    for (int i = 0; i < (months.length - 1); i++) {
	    	monthsMap.put((i <= 8 ? "0" : "") + (new Integer(i+1).toString()), months[i]);
	    }
		
		return monthsMap;
	}
	
	public static String getMonth(String mounth){
		return getMonths().get(mounth);
	}

	public static Date getDateNonSafe(String value) {
		try {
			return ThreadSafeDateParser.parse(value, defaultDateFormat);
		} catch (ParseException e) {
			return null;
		}
	}

	public static String formatDate(Date value) throws ParseException {
		return ThreadSafeDateParser.format(value, defaultDateFormat);
	}
	
	public static String formatDateAmericanNonSafe(Date value) {
		if (value == null)
			return null;
		try {
			return ThreadSafeDateParser.format(value, defaultAmericanFormat);
		} catch (Exception ex) {
			return value.toString();
		}
	}
	
	public static String formatDateTimeAmericanNonSafe(Date value) {
		if (value == null)
			return null;
		try {
			return ThreadSafeDateParser.format(value, defaultAmericanDateTimeFormat);
		} catch (Exception ex) {
			return value.toString();
		}
	}

	public static String formatDateNonSafe(Date value) {
		if (value == null)
			return null;
		try {
			return ThreadSafeDateParser.format(value, defaultDateFormat);
		} catch (Exception ex) {
			return value.toString();
		}
	}

	public static String formatDateTimeNonSafe(Date value) {
		if (value == null)
			return null;
		try {
			return ThreadSafeDateParser.format(value, defaultDateTimeFormat);
		} catch (Exception ex) {
			return value.toString();
		}
	}

	public static Date transformeDate(String data) throws ParseException {
		String datas[] = data.substring(0, 10).split("-");
		String format = datas[2].concat("/").concat(datas[1].concat("/")).concat(datas[0]);

		return getDate(format);
	}

	public static String formatFullDateTimeNonSafe(Date value) {
		if (value == null)
			return null;
		try {
			return ThreadSafeDateParser.format(value, defaultFullDateTimeFormat);
		} catch (Exception ex) {
			return value.toString();
		}
	}
	
	public static Date stringXMLGregorianCalendarToDate(String dateString){
		try{
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			return simpleDateFormat.parse(dateString);
			//Date date = simpleDateFormat.parse("2014-08-04T00:00:00.000-03:00");
			//Date date = simpleDateFormat.parse("2001-07-04T12:08:56.235-0700");
			//System.out.println(DateTimeHelper.dateToXMLGregorianCalendar(date));
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static Date stringXMLGregorianCalendarToHour(String dateString){
		try{
			//SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
			return simpleDateFormat.parse(dateString);
			//Date date = simpleDateFormat.parse("2014-08-04T00:00:00.000-03:00");
			//Date date = simpleDateFormat.parse("2001-07-04T12:08:56.235-0700");
			//System.out.println(DateTimeHelper.dateToXMLGregorianCalendar(date));
		} catch (ParseException e) {
			return null;
		}
	}

	public static Date getTime(String value) throws ParseException {
		return ThreadSafeDateParser.parse(value, defaultTimeFormat);
	}

	public static String formatTime(Date value) throws ParseException {
		return ThreadSafeDateParser.format(value, defaultTimeFormat);
	}

	public static Date getDateTime(String value) throws ParseException {
		return ThreadSafeDateParser.parse(value, defaultDateTimeFormat);
	}

	public static String formatDateTime(Date value) throws ParseException {
		return ThreadSafeDateParser.format(value, defaultDateTimeFormat);
	}

	public static Date getDate(String value, String pattern) throws ParseException {
		return ThreadSafeDateParser.parse(value, pattern);
	}

	public static Date getDateNonSafe(String value, String pattern) {
		try {
			return ThreadSafeDateParser.parse(value, pattern);
		} catch (ParseException e) {
			return null;
		}
	}

	public static String formatDate(Date value, String pattern) throws ParseException {
		return ThreadSafeDateParser.format(value, pattern);
	}

	public static String formatDateNonSafe(Date value, String pattern) {
		if (value == null || pattern == null)
			return null;
		return ThreadSafeDateParser.format(value, pattern);
	}

	public static Date getTime(String value, String pattern) throws ParseException {
		return ThreadSafeDateParser.parse(value, pattern);
	}

	public static String formatTime(Date value, String pattern) throws ParseException {
		return ThreadSafeDateParser.format(value, pattern);
	}

	public static Date getDateTime(String value, String pattern) throws ParseException {
		return ThreadSafeDateParser.parse(value, pattern);
	}

	public static String formatDateTime(Date value, String pattern) throws ParseException {
		return ThreadSafeDateParser.format(value, pattern);
	}

	public static boolean isValid24HourTime(String time) {
		if (StringHelper.isEmptyTrim(time))
			return false;
		return time.matches(defaultTimeMask);
	}

	public static int compareDates(Date date1, Date date2, long factor) {
		if (factor <= 0)
			return 0;
		if (date1 == null) {
			if (date2 == null) {
				return 0;
			} else {
				return -1;
			}
		} else {
			if (date2 == null) {
				return 1;
			} else {

				long diff = (getPureDate(date1).getTime() - getPureDate(date2).getTime()) / factor;
				if (diff > 0)
					return 1;
				else if (diff < 0)
					return -1;
				else
					return 0;
			}
		}
	}

	public static int compareTimes(Date date1, Date date2, long factor) {
		if (factor <= 0)
			return 0;
		if (date1 == null) {
			if (date2 == null) {
				return 0;
			} else {
				return -1;
			}
		} else {
			if (date2 == null) {
				return 1;
			} else {

				long diff = (date1.getTime() - date2.getTime()) / factor;
				if (diff > 0)
					return 1;
				else if (diff < 0)
					return -1;
				else
					return 0;
			}
		}
	}

	public static int differenctInDays(Date date1, Date date2) {
		if (date1 == null || date2 == null) {
			return 0;
		} else {
			long date1MiliDST = getTimeMiliDST(date1, Calendar.DAY_OF_MONTH);
			long date2MiliDST = getTimeMiliDST(date2, Calendar.DAY_OF_MONTH);
			long diff = (date1MiliDST - date2MiliDST) / DIFF_IN_DAYS_FACTOR;
			return (int) diff;
		}
	}

	/**
	 * Retorna a quantidade de milisegundos de uma data aplicando Daylight Saving Time(Diferença de horario de verão), util para calculo de diferença
	 * entre duas datas.
	 * 
	 * @param date
	 * @param truncateField Exempl.o: Dia: Calendar.DAY_OF_MONTH
	 * @return
	 */
	public static long getTimeMiliDST(Date date, int truncateField) {
		long dateMili = DateUtils.truncate(date, truncateField).getTime();
		long dateMiliDST = dateMili + Calendar.getInstance().getTimeZone().getOffset(dateMili);
		return dateMiliDST;
	}
	
	public static double differenctInMonths(Date date1, Date date2) {
		if (date1 == null || date2 == null) {
			return 0;
		} else {
			long date1MiliDST = getTimeMiliDST(date1, Calendar.DAY_OF_MONTH);
			long date2MiliDST = getTimeMiliDST(date2, Calendar.DAY_OF_MONTH);
			double diff = (date1MiliDST - date2MiliDST) / DIFF_IN_MONTHS_FACTOR;
			return diff;
		}
	}

	public static int compareDates(Date date1, Date date2) {
		return compareDates(date1, date2, DIFF_IN_DAYS_FACTOR);
	}

	public static int compareTime(Date time1, Date time2) {
		return compareTimes(time1, time2, DIFF_IN_HOURS_FACTOR);
	}

	// retorna true se data 1 for maior que data 2 - para campos DATE
	// puros....se precisar do time, é outra rotina
	public static boolean isGreaterDate(Date date1, Date date2) {
		long resultado = compareDates(date1, date2);
		return resultado > 0;
	}
	
	static public boolean isAcessoPermitidoByDate(Date data){
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTimeInMillis(data.getTime());
		
		calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
		calendar.set(Calendar.MONTH, Calendar.MAY);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DATE));
		
		return isGreaterDate(calendar.getTime(), new Date()); 
	}
	
	public static Date getDataInicioMes(Date dataReferencia){
		try {
			if(dataReferencia == null)
				return null;
			
			int ano = getYear(dataReferencia);
			int mes = getMonth(dataReferencia)+1;

			return getDate("01/" + mes + "/" + ano);
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static Date getDataFimMes(Date dataReferencia){
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTimeInMillis(dataReferencia.getTime());
		
		calendar.set(Calendar.YEAR, getYear(dataReferencia));
		calendar.set(Calendar.MONTH, getMonth(dataReferencia));
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DATE));
		
		return calendar.getTime();
	}

	public static boolean isGreaterEqualDate(Date date1, Date date2) {
		long resultado = compareDates(date1, date2);
		return resultado >= 0;
	}

	public static boolean isEqualDate(Date date1, Date date2) {
		long resultado = compareDates(date2, date1);
		return resultado == 0;
	}

	public static boolean isDifferentDate(Date date1, Date date2) {
		return !isEqualDate(date1, date2);
	}

	public static boolean isToday(Date date1) {
		long resultado = compareDates(new Date(), date1);
		return resultado == 0;
	}

	// RETORNA TRUE SE DATE 1 É MENOR QUE DATE 2
	public static boolean isSmallerDate(Date date1, Date date2) {
		long resultado = compareDates(date1, date2);
		return resultado < 0;
	}

	public static boolean isSmallerEqualDate(Date date1, Date date2) {
		long resultado = compareDates(date1, date2);
		return resultado <= 0;
	}

	// retorna true se data 1 for maior que data 2 - para campos DATE
	// puros....se precisar do time, é outra rotina
	public static boolean isGreateTime(Date time1, Date time2) {
		long resultado = compareDates(time1, time2);
		return resultado > 0;

	}

	public static boolean isEqualTime(Date time1, Date time2) {
		long resultado = compareTime(time2, time1);
		return resultado == 0;
	}

	// RETORNA TURE SE DATE 1 É MENOR QUE DATE 2
	public static boolean isSmallerTime(Date time1, Date time2) {
		long resultado = compareTime(time1, time2);
		return resultado < 0;
	}

	public static boolean validarDataInicioFim(Date dataInicio, Date dataFim) {
		if (dataFim != null && dataInicio != null) {
			if (isSmallerDate(dataFim, dataInicio)) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean isPrimeiroDiaMes(Date data) {		
		if (data != null) {			
			Calendar primeiroDia = Calendar.getInstance();
			primeiroDia.set(Calendar.DAY_OF_MONTH, 1);			
			return DateTimeHelper.isEqualDate(primeiroDia.getTime(), data);			
		}
		return false;
	}

	public static boolean dataEntre(Date dataInicio, Date dataFim, Date dataEntre) {
		return dataInicio != null && isGreaterEqualDate(dataEntre, dataInicio) && (dataFim == null || isSmallerEqualDate(dataEntre, dataFim));
	}

	public static boolean validarHoraInicioFim(Date horaInicio, Date horaFim) {
		if (horaFim != null && horaInicio != null) {
			if (isSmallerTime(horaFim, horaInicio)) {
				return false;
			}
		}
		return true;
	}

	public static Date addDays(Date date, int days) {
		if (date == null)
			return null;
		return new Date(date.getTime() + DIFF_IN_DAYS_FACTOR * days);
	}
	
	public static Date adicionarAnos(Date dataReferencia, int anos) {
		if (dataReferencia == null) {
			return null;
		}
		Calendar cDate = Calendar.getInstance();
		cDate.setTime(dataReferencia);
		cDate.add(Calendar.YEAR, anos);
		return cDate.getTime();
	}
	
	public static Date removDays(Date date, int days) {
		if (date == null)
			return null;
		return new Date(date.getTime() - DIFF_IN_DAYS_FACTOR * days);
	}

	public static Date addMonths(Date date, int months) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.add(Calendar.MONTH, months);
		return cal.getTime();
	}

	public static Date addSeconds(Date date, int seconds) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.add(Calendar.SECOND, seconds);
		return cal.getTime();
	}
	
	public static Date addMinutes(Date date, int minutes) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, minutes);
		return cal.getTime();
	}

	public static int getYear(Date date) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		return cal.get(Calendar.YEAR);
	}

	public static int getMonth(Date date) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		return cal.get(Calendar.MONTH);
	}

	public static int getDays(Date date) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		return cal.get(Calendar.DAY_OF_WEEK_IN_MONTH);
	}
	
	public static int getDay(Date date) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		return cal.get(Calendar.DAY_OF_MONTH);
	}

	// volta um date, com a parte do time zerado, muito util para buscas com
	// date
	public static Date getPureDate(Date date) {
		if (date == null)
			return null;
		try {
			return getDate(formatDateNonSafe(date));
		} catch (ParseException e) {
			return date;
		}
	}

	public static final Date getAmanha() {
		return DateTimeHelper.addDays(getPureDate(new Date()), +1);
	}

	public static final Date getOntem() {
		return DateTimeHelper.addDays(getPureDate(new Date()), -1);
	}

	public static final int idadeEmAnos(Date dataNascimento) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(dataNascimento);
		Calendar now = new GregorianCalendar();
		int res = now.get(Calendar.YEAR) - cal.get(Calendar.YEAR);
		if ((cal.get(Calendar.MONTH) > now.get(Calendar.MONTH)) || (cal.get(Calendar.MONTH) == now.get(Calendar.MONTH) && cal.get(Calendar.DAY_OF_MONTH) > now.get(Calendar.DAY_OF_MONTH))) {
			res--;
		}
		return res;
	}

	public static final int idadeEmMeses(Date dataReferencia) {
		Calendar dataNascimento = new GregorianCalendar();
		dataNascimento.setTime(dataReferencia);

		Calendar dataCorrente = Calendar.getInstance();
		int month = dataNascimento.get(Calendar.MONTH);
		int day = dataNascimento.get(Calendar.DAY_OF_MONTH);

		int diferencaEmAnos = dataCorrente.get(Calendar.YEAR) - dataNascimento.get(Calendar.YEAR);
		int diferencaEmMeses = 0;
		int diferencaEmDias = 0;
		if (dataCorrente.before(new GregorianCalendar(dataCorrente.get(Calendar.YEAR), month, day))) {
			// dataNascimento com o mês/dia pós data atual
			diferencaEmAnos--;
			diferencaEmMeses = (12 - (dataNascimento.get(Calendar.MONTH) + 1)) + (dataNascimento.get(Calendar.MONTH));
			if (day > dataCorrente.get(Calendar.DAY_OF_MONTH)) {
				diferencaEmDias = day - dataCorrente.get(Calendar.DAY_OF_MONTH);
			} else if (day < dataCorrente.get(Calendar.DAY_OF_MONTH)) {
				diferencaEmDias = dataCorrente.get(Calendar.DAY_OF_MONTH) - day;
			}
		} else if (dataCorrente.after(new GregorianCalendar(dataCorrente.get(Calendar.YEAR), month, day))) {
			diferencaEmMeses = (dataCorrente.get(Calendar.MONTH) - (dataNascimento.get(Calendar.MONTH)));
			if (day > dataCorrente.get(Calendar.DAY_OF_MONTH))
				diferencaEmDias = day - dataCorrente.get(Calendar.DAY_OF_MONTH);
			else if (day < dataCorrente.get(Calendar.DAY_OF_MONTH)) {
				diferencaEmDias = dataCorrente.get(Calendar.DAY_OF_MONTH) - day;
			}
		}
		// corrige o mês, caso não tenha completado (pelos dias)
		if (diferencaEmDias > 0 && diferencaEmMeses > 0) {
			diferencaEmMeses--;
		}

		return diferencaEmAnos * 12 + diferencaEmMeses;
	}
	
	public static final int calculaMeses(final Date inicio, final Date fim){
		Calendar dataInicio = new GregorianCalendar(); 
		Calendar dataFim = new GregorianCalendar();
		dataInicio.setTime(inicio);
		dataFim.setTime(fim);
		
		int mesesAnos = 0;
		int mesesMeses = 0;
		
		mesesAnos = (dataFim.get(Calendar.YEAR) - dataInicio.get(Calendar.YEAR)) * 12;;
		if(dataFim.get(Calendar.MONTH) >= dataInicio.get(Calendar.MONTH)){
			if(dataFim.get(Calendar.DAY_OF_MONTH) < dataInicio.get(Calendar.DAY_OF_MONTH)){
				mesesMeses = dataFim.get(Calendar.MONTH) - dataInicio.get(Calendar.MONTH) -1;
			}else{
				mesesMeses = dataFim.get(Calendar.MONTH) - dataInicio.get(Calendar.MONTH);
			}
		}else{
			if(dataFim.get(Calendar.DAY_OF_MONTH) <= dataInicio.get(Calendar.DAY_OF_MONTH)){
				mesesAnos = mesesAnos + (dataFim.get(Calendar.MONTH) - dataInicio.get(Calendar.MONTH)) + 1;
			}else{
				mesesAnos = mesesAnos + (dataFim.get(Calendar.MONTH) - dataInicio.get(Calendar.MONTH));
			}
		}
		
		return mesesAnos + mesesMeses;
	}

	public static Date[] getInicioFimAno(Date dataReferencia) {
		if (dataReferencia == null)
			return null;
		String ano = "" + DateTimeHelper.getYear(dataReferencia);
		Date inicioDoAno = DateTimeHelper.getDateNonSafe("01/01/" + ano);
		Date fimDoAno = DateTimeHelper.getDateNonSafe("31/12/" + ano);
		return new Date[] { inicioDoAno, fimDoAno };
	}
	
	public static Date[] getInicioFimPeriodo(Date dataReferencia) {
		if (dataReferencia == null)
			return null;
		return new Date[] { addDays(addMonths(dataReferencia, -1), 1), dataReferencia };
	}

	public static Date[] getInicioFimMes(Date dataReferencia) {
		if (dataReferencia == null)
			return null;
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(dataReferencia);
		int primeiroDia = cal.getActualMinimum(GregorianCalendar.DAY_OF_MONTH);
		int ultimoDia = cal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
		int ano = cal.get(Calendar.YEAR);
		int mes = cal.get(Calendar.MONTH) + 1;
		Date inicioDoMes = DateTimeHelper.getDateNonSafe("" + primeiroDia + "/" + mes + "/" + ano);
		Date fimDoMes = DateTimeHelper.getDateNonSafe("" + ultimoDia + "/" + mes + "/" + ano);
		return new Date[] { inicioDoMes, fimDoMes };
	}

	public static Date getPrimeiroDiaSemana(Date dataReferencia) {
		Calendar primDiaSemana = Calendar.getInstance();
		primDiaSemana.setTime(dataReferencia);
		primDiaSemana.add(Calendar.DAY_OF_MONTH, -(primDiaSemana.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY));
		primDiaSemana = DateUtils.truncate(primDiaSemana, Calendar.DAY_OF_MONTH);
		return primDiaSemana.getTime();
	}

	public static Date getUltimoDiaSemana(Date dataReferencia) {
		Calendar ultDiaSemana = Calendar.getInstance();
		ultDiaSemana.setTime(dataReferencia);
		ultDiaSemana.add(Calendar.DAY_OF_MONTH, Calendar.SATURDAY - ultDiaSemana.get(Calendar.DAY_OF_WEEK));
		ultDiaSemana = DateUtils.truncate(ultDiaSemana, Calendar.DAY_OF_MONTH);
		ultDiaSemana.add(Calendar.DAY_OF_MONTH, 1);
		ultDiaSemana.add(Calendar.MILLISECOND, -1);
		return ultDiaSemana.getTime();
	}

	public static List<Date> dateConditionVirgula(String datas) throws ServiceException {

		String[] vals = datas.toString().split(",");
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		df.setLenient(false);
		List<Date> data = new ArrayList<Date>();

		if (vals.length != 2) {
			throw ExceptionHandler.getInstance().generateException(ServiceException.class, ExceptionConstants.K_INVALID_FIELD_VALUE, "Digite duas datas");
		}

		try {
			data.add(df.parse(vals[0]));
		} catch (Exception e) {
			throw ExceptionHandler.getInstance().generateException(ServiceException.class, ExceptionConstants.K_INVALID_FIELD_VALUE, "Data Início Inválida");
		}
		try {
			data.add(df.parse(vals[1]));
		} catch (ParseException e) {
			throw ExceptionHandler.getInstance().generateException(ServiceException.class, ExceptionConstants.K_INVALID_FIELD_VALUE, "Data Final Inválida");
		}

		return data;
	}

	public static void main(String[] args) {
	}

	public static Date getBusinessDayOfWeekAfter(int days) {
		GregorianCalendar hoje = new GregorianCalendar();
		int dow = hoje.get(Calendar.DAY_OF_WEEK);
		int daysToAdd = dow == Calendar.THURSDAY ? 2 + days : dow == Calendar.FRIDAY ? 2 + days : dow == Calendar.SATURDAY ? 1 + days : dow == Calendar.SUNDAY ? days : 0;
		return addDays(hoje.getTime(), daysToAdd);
	}

	public static Date montarDataBaseadoNoDia(Date dataReferencia, int diaVencimentoMensalidade, boolean naoPodeEstarVencido) {
		Date returnValue = montarDataBaseadoNoDia(dataReferencia, diaVencimentoMensalidade);
		if (DateTimeHelper.isSmallerEqualDate(returnValue, dataReferencia) || DateTimeHelper.isSmallerEqualDate(returnValue, new Date())) {
			for (int i = 1; i <= 12; i++) {
				returnValue = montarDataBaseadoNoDia(DateTimeHelper.addMonths(dataReferencia, i), diaVencimentoMensalidade);
				if (DateTimeHelper.isGreaterDate(returnValue, dataReferencia) || DateTimeHelper.isGreaterDate(returnValue, new Date())) {
					break;
				}
			}
		}
		return returnValue;
	}
	
	public static Date montarDataBaseadoNoDia(Date dataReferencia, int diaVencimentoMensalidade) {
		// baseado na referencia, tenta montar um vencimento
		Calendar referencia = new GregorianCalendar();
		referencia.setTime(dataReferencia);

		// pegar o dia
		int mesReferencia = referencia.get(Calendar.MONTH) + 1;
		int diaReferencia = diaVencimentoMensalidade;
		int anoReferencia = referencia.get(Calendar.YEAR);
		switch (referencia.get(Calendar.MONTH)) {
		case Calendar.FEBRUARY:
			if (diaVencimentoMensalidade > 28) {
				diaReferencia = 1;
				mesReferencia++;
			}
			break;
		case Calendar.APRIL:
		case Calendar.JUNE:
		case Calendar.SEPTEMBER:
		case Calendar.NOVEMBER:
			if (diaVencimentoMensalidade > 30) {
				diaReferencia = 1;
				mesReferencia++;
			}
			break;
		}

		try {
			Date returnValue = getDate(diaReferencia + "/" + mesReferencia + "/" + anoReferencia);
			return returnValue;
		} catch (ParseException e) {
			return montarDataBaseadoNoDia(dataReferencia, diaVencimentoMensalidade + 1);
		}

	}	
	
	public static final boolean maximoDiasPermitido(List<Date> Datas, String operador, int qtdDiasPermitido) throws ServiceException {
		if(Datas.size() == 2)
			return maximoDiasPermitido(Datas.get(0), Datas.get(1), operador, qtdDiasPermitido);
		if(Datas.size() == 1)
			return maximoDiasPermitido(Datas.get(0), null, operador, qtdDiasPermitido);
		throw ExceptionHandler.getInstance().generateException(ServiceException.class, ExceptionConstants.K_INVALID_FIELD_VALUE, "Necessário informar uma data!");
	}
	
	public static final boolean maximoDiasPermitido(Date data1, Date data2, String operador, int qtdDiasPermitido) throws ServiceException {
		int qtdDias = 0;
		if(data1 == null)
			throw ExceptionHandler.getInstance().generateException(ServiceException.class, ExceptionConstants.K_INVALID_FIELD_VALUE, "Necessário informar uma data!");

		if (operador.equals("BETWEEN")){
			if(data2 != null){
				qtdDias = DateTimeHelper.differenctInDays(data2,data1);
			}else{
				throw ExceptionHandler.getInstance().generateException(ServiceException.class, ExceptionConstants.K_INVALID_FIELD_VALUE, "Necessário informar duas datas!");
			}
		}
		
		if(operador.equals(">") || operador.equals(">=") || operador.equals("(*) >") || operador.equals("(*) >=")){
			qtdDias = DateTimeHelper.differenctInDays(new Date(),data1);
		}
		
		if(operador.equals("<") || operador.equals("<=") || operador.equals("(*) <") || operador.equals("(*) <=") || operador.equals("!=") || operador.equals("(*) !=")){
			return false;
		}
		
		if(operador.equals("=")  || operador.equals("(*) =") || operador.equals("IN") ){
			return true;
		}
		
		return qtdDias < qtdDiasPermitido;
	}

	public static XMLGregorianCalendar parseDateToXmlGregorianCalendar(Date date) throws DatatypeConfigurationException {
		if (date == null) {
			return null;
		}
		GregorianCalendar gregorianCalendar = new GregorianCalendar();
		gregorianCalendar.setTime(date);
		XMLGregorianCalendar xmlGrogerianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
		return xmlGrogerianCalendar;
	}
	
	public static XMLGregorianCalendar parseDateToXmlGregorianCalendarTime(Date date) throws DatatypeConfigurationException {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		XMLGregorianCalendar xmlGrogerianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendarTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND), DatatypeConstants.FIELD_UNDEFINED);
		return xmlGrogerianCalendar;
	}	
	
	public static XMLGregorianCalendar parseDateToXmlGregorianCalendarDate(Date date) throws DatatypeConfigurationException {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		XMLGregorianCalendar xmlGrogerianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), DatatypeConstants.FIELD_UNDEFINED);
		return xmlGrogerianCalendar;
	}
	
	public static Date parseXmlGregorianCalendarToDate(XMLGregorianCalendar date){
		if (date == null) {
			return null;
		}
		return date.toGregorianCalendar().getTime();
	}
	
	public static XMLGregorianCalendar dateToXMLGregorianCalendar(Date date){
		try {
			return DatatypeFactory.newInstance().newXMLGregorianCalendar(DateTimeHelper.dateToGregorianCalendar(date));
		} catch (DatatypeConfigurationException e) {
			return null;
		}
	}
	
	public static GregorianCalendar dateToGregorianCalendar(Date date){
		
		if(date == null){
			return null;
		}
	
		GregorianCalendar data = new GregorianCalendar();
		data.setTime(date);
	
		return data;
	}
}

class ThreadSafeDateParser {

	private static final ThreadLocal<Map<String, DateFormat>> PARSERS = new ThreadLocal<Map<String, DateFormat>>() {
		protected Map<String, DateFormat> initialValue() {
			return new Hashtable<String, DateFormat>();
		}
	};

	static private final DateFormat getParser(final String pattern) {
		Map<String, DateFormat> parserMap = PARSERS.get();
		DateFormat df = parserMap.get(pattern);
		if (null == df) {
			if (log.isDebugEnabled()) {
				log.debug("Date Format Pattern " + pattern + " not found in current thread:" + Thread.currentThread().getId());
			}
			// if parser for the same pattern does not exist yet, create one and
			// save it into map
			df = new SimpleDateFormat(pattern);
			parserMap.put(pattern, df);
		}
		return df;
	}

	static public Date parse(final String date, final String pattern) throws ParseException {
		try {
			return getParser(pattern).parse(date);
		} catch (Exception e) {
			throw new ParseException("Data invalida, formato incorreto!", 0);
		}
	}

	static public long parseLongDate(final String date, final String pattern) throws ParseException {
		return parse(date, pattern).getTime();
	}

	static public String format(final Date date, final String pattern) {
		return getParser(pattern).format(date);
	}

	static public String format(final long date, final String pattern) {
		return getParser(pattern).format(new Date(date));
	}
	
	static final private Logger log = Logger.getLogger(ThreadSafeDateParser.class);
	
	public static Date montarDataFechamentoBaseadoNoDia(Date dataReferencia, int diaEmissao) {
		// baseado na referencia, tenta montar um vencimento
		Calendar referencia = new GregorianCalendar();
		referencia.setTime(dataReferencia);

		// pegar o dia
		int mesReferencia = referencia.get(Calendar.MONTH) + 1;
		int diaReferencia = diaEmissao;
		int anoReferencia = referencia.get(Calendar.YEAR);
		switch (referencia.get(Calendar.MONTH)) {
		case Calendar.FEBRUARY:
			if (diaEmissao > referencia.getActualMaximum(Calendar.DAY_OF_MONTH)) {
				diaReferencia = referencia.getActualMaximum(Calendar.DAY_OF_MONTH);
			}
			break;
		case Calendar.APRIL:
		case Calendar.JUNE:
		case Calendar.SEPTEMBER:
		case Calendar.NOVEMBER:
			if (diaEmissao > referencia.getActualMaximum(Calendar.DAY_OF_MONTH)) {
				diaReferencia = referencia.getActualMaximum(Calendar.DAY_OF_MONTH);
			}
			break;
		}

		try {
			Date returnValue = DateTimeHelper.getDate(diaReferencia + "/" + mesReferencia + "/" + anoReferencia);
			return returnValue;
		} catch (ParseException e) {
			return montarDataFechamentoBaseadoNoDia(dataReferencia, diaEmissao - 1);
		}
	}
	
	
}