package org.override.services;

import lombok.extern.log4j.Log4j2;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.override.core.models.HyperEntity;
import org.override.core.models.HyperException;
import org.override.models.StudentSummary;
import org.override.models.TermResult;
import org.override.models.TermScoreItem;
import org.override.models.TermScoreSummary;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static java.lang.System.err;

@Log4j2
@Service
public class TermResultService {
    /**
     * header: {
     * "mssv": "3118410488"
     * }
     */
    public static final String urlLogin = "http://thongtindaotao.sgu.edu.vn/default.aspx?page=nhapmasv&flag=XemDiemThi";
    public static final String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36";
    public static final String STUDENT_ID = "studentId";

    public HyperEntity handleRequest(Map<String, String> headers) {
        String mssv = headers.get(STUDENT_ID);
        if (mssv == null) {
            return HyperEntity.badRequest(
                    new HyperException(
                            HyperException.BAD_REQUEST,
                            "headers -> mssv",
                            "detail studentId at headers is required"
                    )
            );
        }
        return getTermResult(mssv);
    }

    public HyperEntity getTermResult(String mssv) {
//        TODO: dùng jsoup lấy dữ liệu
        TermResult termResult = termR(mssv);
        return HyperEntity.ok(termResult);
    }

    public String getNgsinh(String ms) {
        StringBuilder result = new StringBuilder();
        String url = "http://thongtindaotao.sgu.edu.vn/Default.aspx?page=thoikhoabieu&sta=1&id=";
        try {
            Connection.Response response = Jsoup.connect(url + ms)
                    .ignoreContentType(true)
                    .method(Connection.Method.GET).execute();
            Document document = response.parse();
            String name_dateOfBirth = document.getElementById("ctl00_ContentPlaceHolder1_ctl00_lblContentTenSV").text();

            String[] parts = name_dateOfBirth.split(":");
            String date = parts[1];
            log.info(date);

            result.append(date);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();

    }

    public TermResult termR(String mssv) {
        TermResult termResult = new TermResult();
        TermResult.TermResultItem termResultItem = new TermResult.TermResultItem();
        List<TermResult.TermResultItem> ListTermResultItems = new ArrayList<>();
        StringBuilder result = new StringBuilder();
        try {
            Connection.Response response = Jsoup.connect(urlLogin)
                    .method(Connection.Method.GET)
                    .execute();
            Document loginPage = response.parse();
            response = Jsoup.connect(urlLogin)
                    .data("__EVENTTARGET", "").data("__EVENTARGUMENT", "")
                    .data("__VIEWSTATE", "").data("__VIEWSTATEGENERATOR", "")
                    .data("ctl00$ContentPlaceHolder1$ctl00$txtMaSV", mssv)
                    .data("ctl00$ContentPlaceHolder1$ctl00$btnOK", "OK")
                    .userAgent(userAgent)
                    .timeout(0)
                    .followRedirects(true)
                    .cookies(response.cookies())
                    .method(Connection.Method.GET)
                    .execute();
            loginPage = response.parse();
            String name = loginPage.getElementById("ctl00_ContentPlaceHolder1_ctl00_ucThongTinSV_lblTenSinhVien").text();
            String gender = loginPage.getElementById("ctl00_ContentPlaceHolder1_ctl00_ucThongTinSV_lblPhai").text();
            String placeOfBirth = loginPage.getElementById("ctl00_ContentPlaceHolder1_ctl00_ucThongTinSV_lblNoiSinh").text();
            String Class = loginPage.getElementById("ctl00_ContentPlaceHolder1_ctl00_ucThongTinSV_lblLop").text();
            String majors = loginPage.getElementById("ctl00_ContentPlaceHolder1_ctl00_ucThongTinSV_lbNganh").text();


//            String s = String.format("%s | %s | %s | %s | %s | %s \n", name, getNgsinh(mssv), gender, placeOfBirth, Class, majors);
//            result.append(s);
//            System.out.println(s);

            StudentSummary studentSummary = new StudentSummary();
            studentSummary.setId(mssv);
            studentSummary.setName(name);
            studentSummary.setGender(gender);
            studentSummary.setDateOfBirth(getNgsinh(mssv));
            studentSummary.setPlaceOfBirth(placeOfBirth);
            studentSummary.setClasses(Class);
            studentSummary.setSubject(majors);

            List<TermScoreItem> ListTermScoreItems = new ArrayList<>();
            TermScoreItem termScoreItem = new TermScoreItem();


            List<Element> tables = loginPage.getElementsByClass("view-table");
            Element table = tables.get(0);
            Elements rows = table.select("tr");
            Iterator<Element> itr = rows.iterator();
            while (itr.hasNext()) {
                Element row = itr.next();
                List<String> term = new ArrayList<String>();
                while (row.getElementsByClass("title-hk-diem").text() != "") {
                    term.add(row.getElementsByClass("title-hk-diem").text());
                    break;
                }

                for (int i = 0; i < term.size() - 1; i++) {
                    System.out.println(term.get(i));
                    String[] parts = term.get(i).split("-");
                    termResultItem.setTerm(Integer.parseInt(parts[0].replaceAll("[^0-9,-\\.]", "")));
                    termResultItem.setYear(parts[1].replaceAll("[^0-9,-\\.]", ""));
                    ListTermScoreItems.add(GettermScoreItem(mssv, term.get(i)));

                }
            }

        } catch (IOException e) {
            err.println(e);
        }
        return termResult;
    }

    private TermScoreItem GettermScoreItem(String message, String term) {
        TermScoreItem termScoreItem = new TermScoreItem();

        StringBuilder result = new StringBuilder();
        try {
            Document document = Jsoup.connect(String.format(term, message)).get();

            List<Element> tables = document.getElementsByClass("view-table");

            Element table = tables.get(0);
            Elements rows = table.select("tr");
            Iterator<Element> itr = rows.iterator();
            while (itr.hasNext()) {
                Element row = itr.next();
                if (row.text().equals(term)) {
                    Element currentRow;
                    while ((currentRow = itr.next()) != null) {
                        if (currentRow.classNames().contains("row-diemTK") ||
                                !currentRow.classNames().contains("row-diem") &&
                                        !currentRow.classNames().contains("row-diemTK")) {
                            break;
                        } else {

                            Elements cols = currentRow.select("td");
                            termScoreItem.setSubjectId(cols.get(1).text());
                            termScoreItem.setSubjectName(cols.get(2).text());
                            termScoreItem.setCreditsCount(Integer.parseInt(cols.get(3).text()));
                            termScoreItem.setExamPercent(Double.parseDouble(cols.get(4).text()));
                            termScoreItem.setFinalExamPercent(Double.parseDouble(cols.get(5).text()));
                            termScoreItem.setExamScore(Double.parseDouble(cols.get(6).text()));
                            termScoreItem.setFinalExamScore(Double.parseDouble(cols.get(7).text()));
                            termScoreItem.setTermScoreFirst(Double.parseDouble(cols.get(8).text()));
                            termScoreItem.setTermScoreSecond(Double.parseDouble(cols.get(9).text()));
                            termScoreItem.setGpaFirst(cols.get(10).text());
                            termScoreItem.setGpaSecond(cols.get(11).text());
                            termScoreItem.setGpa(Double.parseDouble(cols.get(12).text()));
                            termScoreItem.setResult(cols.get(13).text());

                            result.append(
                                    String.format("%s | %s | %s \n",
                                            cols.get(1).text(),
                                            cols.get(2).text(),
                                            cols.get(9).text()
                                    )
                            );
                        }

                    }

                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return termScoreItem;
    }

    private TermScoreSummary GettermScoreSummary(String mssv, String term) {
        TermScoreSummary termScoreSummary = new TermScoreSummary();

        return termScoreSummary;
    }


}
