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
import org.override.utils.Utils;
import org.springframework.stereotype.Service;

import java.io.IOException;
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

    final String termResultItemTitleClassName = "title-hk-diem";
    final String termScoreItemClassName = "row-diem";
    final String termScoreSummaryClassName = "row-diemTK";
    private final String span = "span";

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
        return termR(mssv);
//        return HyperEntity.ok(termResult);
    }

    public HyperEntity termR(String mssv) {
        TermResult termResult = new TermResult();
//        TermResult.TermResultItem termResultItem = new TermResult.TermResultItem();
//        List<TermResult.TermResultItem> ListTermResultItems = new ArrayList<>();
//        List<TermScoreItem> ListTermScoreItems = new ArrayList<>();
        try {
            Connection.Response response = Jsoup.connect(urlLogin)
                    .method(Connection.Method.GET)
                    .execute();
            Document loginPage = response.parse();

            response = Jsoup.connect(urlLogin)
                    .data("__EVENTTARGET", "")
                    .data("__EVENTARGUMENT", "")
                    .data("__VIEWSTATE", "")
                    .data("__VIEWSTATEGENERATOR", "")
                    .data("ctl00$ContentPlaceHolder1$ctl00$txtMaSV", mssv)
                    .data("ctl00$ContentPlaceHolder1$ctl00$btnOK", "OK")
                    .userAgent(userAgent)
                    .timeout(0)
                    .followRedirects(true)
                    .cookies(response.cookies())
                    .method(Connection.Method.GET)
                    .execute();

            Document document = response.parse();

            List<Element> tables = document.getElementsByClass("view-table");
            if (tables.size() == 0) {
                return HyperEntity.badRequest(
                        new HyperException(
                                HyperException.BAD_REQUEST,
                                "headers -> mssv",
                                "studentId not found"
                        ));
            }

            String name = document.getElementById("ctl00_ContentPlaceHolder1_ctl00_ucThongTinSV_lblTenSinhVien").text();
            String gender = document.getElementById("ctl00_ContentPlaceHolder1_ctl00_ucThongTinSV_lblPhai").text();
            String placeOfBirth = document.getElementById("ctl00_ContentPlaceHolder1_ctl00_ucThongTinSV_lblNoiSinh").text();
            String clazz = document.getElementById("ctl00_ContentPlaceHolder1_ctl00_ucThongTinSV_lblLop").text();
            String majors = document.getElementById("ctl00_ContentPlaceHolder1_ctl00_ucThongTinSV_lbNganh").text();
            Element elSpeciality = document.getElementById("ctl00_ContentPlaceHolder1_ctl00_ucThongTinSV_lblChNg");
            String speciality = elSpeciality != null ? elSpeciality.text() : null;

            StudentSummary studentSummary = new StudentSummary();
            studentSummary.setId(mssv);
            studentSummary.setName(name);
            studentSummary.setGender(gender);
            studentSummary.setDateOfBirth(getNgsinh(mssv));
            studentSummary.setPlaceOfBirth(placeOfBirth);
            studentSummary.setClasses(clazz);
            studentSummary.setSubject(majors);
            studentSummary.setSpeciality(speciality);
            termResult.setStudentSummary(studentSummary);


            Element table = tables.get(0);
            Elements rows = table.select("tr");
            Iterator<Element> itr = rows.iterator();
            TermResult.TermResultItem currentTermResulItem = new TermResult.TermResultItem();
            while (itr.hasNext()) {
                Element row = itr.next();
//                ROW HOC KY
                if (row.hasClass(termResultItemTitleClassName)) {
                    currentTermResulItem = new TermResult.TermResultItem();
                    String[] parts = row.text().split("-");
                    currentTermResulItem.setTerm(Integer.parseInt(parts[0].replaceAll("[^0-9,-\\.]", "")));
                    currentTermResulItem.setYear(parts[1].replaceAll("[^0-9,-\\.]", ""));

                    termResult.termResultItems.add(currentTermResulItem);
                    continue;
                }
//                ROW DIEM MON HOC
                if (row.hasClass(termScoreItemClassName)) {
                    Elements cols = row.select("td");
                    TermScoreItem termScoreItem = new TermScoreItem();
                    termScoreItem.setSubjectId(cols.get(1).text());
                    termScoreItem.setSubjectName(cols.get(2).text());
                    termScoreItem.setCreditsCount(
                            Integer.parseInt(cols.get(3).text())
                    );
                    termScoreItem.setExamPercent(
                            Utils.parseDouble(cols.get(4).text()).orElse(null)
                    );
                    termScoreItem.setFinalExamPercent(
                            Utils.parseDouble(cols.get(5).text()).orElse(null)
                    );
                    termScoreItem.setExamScore(
                            Utils.parseDouble(cols.get(6).text()).orElse(null)
                    );
                    termScoreItem.setFinalExamScore(Utils.parseDouble(cols.get(7).text()).orElse(null));
                    termScoreItem.setTermScoreFirst(Utils.parseDouble(cols.get(8).text()).orElse(null));
                    termScoreItem.setTermScoreSecond(Utils.parseDouble(cols.get(9).text()).orElse(null));
                    termScoreItem.setGpaFirst(cols.get(10).text());
                    termScoreItem.setGpaSecond(cols.get(11).text());
                    termScoreItem.setGpa(Utils.parseDouble(cols.get(12).text()).orElse(null));
                    termScoreItem.setResult(cols.get(13).text());
                    currentTermResulItem.termScoreItems.add(
                            termScoreItem
                    );
                    continue;
                }
//                ROW DIEM TONG KET
                if (row.hasClass(termScoreSummaryClassName)) {
                    TermScoreSummary termScoreSummary = new TermScoreSummary();
                    Elements cols = row.select("td");
                    if (cols.get(0).text().contains("Điểm trung bình học kỳ hệ 10/100:")) {
                        termScoreSummary.setAvgTermScore(
                                Utils.parseDouble(
                                        cols.get(0).getElementsByTag(span).get(1).text()
                                ).orElse(null)
                        );
                    }
                    row = itr.next();
                    cols = row.select("td");
                    if (cols.get(0).text().contains("Điểm trung bình học kỳ hệ 4:")) {
                        termScoreSummary.setAvgGPATermScore(
                                Utils.parseDouble(
                                        cols.get(0).getElementsByTag(span).get(1).text()
                                ).orElse(null)
                        );
                    }
                    row = itr.next();
                    cols = row.select("td");
                    if (cols.get(0).text().contains("Điểm trung bình tích lũy:")) {
                        termScoreSummary.setAvgScore(
                                Utils.parseDouble(
                                        cols.get(0).getElementsByTag(span).get(1).text()
                                ).orElse(null)
                        );
                    }
                    row = itr.next();
                    cols = row.select("td");
                    if (cols.get(0).text().contains("Điểm trung bình tích lũy (hệ 4):")) {
                        termScoreSummary.setAvgGPAScore(
                                Utils.parseDouble(
                                        cols.get(0).getElementsByTag(span).get(1).text()
                                ).orElse(null)
                        );
                    }
                    row = itr.next();
                    cols = row.select("td");
                    if (cols.get(0).text().contains("Số tín chỉ đạt:")) {
                        termScoreSummary.setCreditsTermCount(
                                Utils.parseInteger(
                                        cols.get(0).getElementsByTag(span).get(1).text()
                                ).orElse(null)
                        );
                    }
                    row = itr.next();
                    cols = row.select("td");
                    if (cols.get(0).text().contains("Số tín chỉ tích lũy:")) {
                        termScoreSummary.setCreditsCount(
                                Utils.parseInteger(
                                        cols.get(0).getElementsByTag(span).get(1).text()
                                ).orElse(null)
                        );
                    }
                    currentTermResulItem.setTermScoreSummary(termScoreSummary);
                }

            }
        } catch (IOException e) {
            err.println(e);
        }
        termResult.setStudentSummary(termResult.studentSummary);
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

            result.append(date);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();

    }

}
