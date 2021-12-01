

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.override.core.models.HyperEntity;
import org.override.core.models.HyperException;
import org.override.models.TermResult;
import org.override.models.TermScoreItem;
import org.override.utils.FakeData;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static java.lang.System.err;

@Log4j2
@Service
public class test1 {
    /**
     * header: {
     * "mssv": "3118410488"
     * }
     */

    public static final String URL = "http://thongtindaotao.sgu.edu.vn/Default.aspx?page=xemdiemthi&id=%s";
    public static final String TERM = "Học kỳ 2 - Năm học 2020-2021";
    public static final String urlLogin = "http://thongtindaotao.sgu.edu.vn/default.aspx?page=nhapmasv&flag=XemDiemThi";
    public static final String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36";
    public static final Gson gson = new Gson();

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

    private String lookupAcademicResults(String message) {
        StringBuilder result = new StringBuilder();
        try {

            Connection.Response response = Jsoup.connect(urlLogin)
                    .method(Connection.Method.GET)
                    .execute();
            Document loginPage = response.parse();
            response = Jsoup.connect(urlLogin)
                    .data("__EVENTTARGET", "").data("__EVENTARGUMENT", "")
                    .data("__VIEWSTATE", "").data("__VIEWSTATEGENERATOR", "")
                    .data("ctl00$ContentPlaceHolder1$ctl00$txtMaSV", message)
                    .data("ctl00$ContentPlaceHolder1$ctl00$btnOK", "OK")
                    .userAgent(userAgent)
                    .timeout(0)
                    .followRedirects(true)
                    .cookies(response.cookies())
                    .method(Connection.Method.GET)
                    .execute();
            Document document = response.parse();

            String name = document.getElementById("ctl00_ContentPlaceHolder1_ctl00_ucThongTinSV_lblTenSinhVien").text();
            String gender = document.getElementById("ctl00_ContentPlaceHolder1_ctl00_ucThongTinSV_lblPhai").text();
            String placeOfBirth = document.getElementById("ctl00_ContentPlaceHolder1_ctl00_ucThongTinSV_lblNoiSinh").text();
            String Class = document.getElementById("ctl00_ContentPlaceHolder1_ctl00_ucThongTinSV_lblLop").text();
            String majors = document.getElementById("ctl00_ContentPlaceHolder1_ctl00_ucThongTinSV_lbNganh").text();

            String s = String.format("%s | %s | %s | %s | %s | %s \n", name, getNgsinh(message), gender, placeOfBirth, Class, majors);
            result.append(s);
            System.out.println(s);
            List<Element> tables = document.getElementsByClass("view-table");
            if (tables.size() == 0) {
                return "NOT FOUND";
            }

            Element table = tables.get(0);
            Elements rows = table.select("tr");
            Iterator<Element> itr = rows.iterator();
            while (itr.hasNext()) {
                Element row = itr.next();
                if (row.text().equals(TERM)) {
                    Element currentRow;
                    while ((currentRow = itr.next()) != null) {
                        if (currentRow.classNames().contains("row-diemTK")) {
                            break;
                        }
                        Elements cols = currentRow.select("td");
                        result.append(
                                String.format("%s | %s | %s \n",
                                        cols.get(1).text(),
                                        cols.get(2).text(),
                                        cols.get(9).text()
                                )
                        );

                    }
                    while ((currentRow = itr.next()) != null) {
                        Elements cols = currentRow.select("td");
                        if (currentRow.classNames().contains("row-diemTK")) {
                          //  result.append(cols.get(0).text()+"|");
                            if (cols.get(0).text().contains("Số tín chỉ tích lũy:")) {
                                break;
                            }

                        }
                        if (cols.get(0).text().contains("Số tín chỉ tích lũy:")) {
                            break;
                        }

                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }


    private String GettermScoreSummary(String message) {

        StringBuilder result = new StringBuilder();
        try {

            Connection.Response response = Jsoup.connect(urlLogin)
                    .method(Connection.Method.GET)
                    .execute();
            Document loginPage = response.parse();
            response = Jsoup.connect(urlLogin)
                    .data("__EVENTTARGET", "").data("__EVENTARGUMENT", "")
                    .data("__VIEWSTATE", "").data("__VIEWSTATEGENERATOR", "")
                    .data("ctl00$ContentPlaceHolder1$ctl00$txtMaSV", message)
                    .data("ctl00$ContentPlaceHolder1$ctl00$btnOK", "OK")
                    .userAgent(userAgent)
                    .timeout(0)
                    .followRedirects(true)
                    .cookies(response.cookies())
                    .method(Connection.Method.GET)
                    .execute();
            Document document = response.parse();

            List<Element> tables = document.getElementsByClass("view-table");

            Element table = tables.get(0);
            Elements rows = table.select("tr");
            Iterator<Element> itr = rows.iterator();
            while (itr.hasNext()) {
                Element row = itr.next();
                if (row.text().equals(TERM)){
                    Element currentRow;
                    while ((currentRow = itr.next()) != null) {

                        Elements cols = currentRow.select("td");
                        if (currentRow.classNames().contains("row-diemTK")) {
                         //   result.append(cols.get(0).text() + "|");
                            if (cols.get(0).text().contains("Điểm trung bình học kỳ hệ 10/100:")) {
                                System.out.println(cols.get(0).text());

                            }
                            if (cols.get(0).text().contains("Điểm trung bình học kỳ hệ 4:")) {
                                System.out.println(cols.get(0).text());

                            }
                            if (cols.get(0).text().contains("Điểm trung bình tích lũy:")) {
                                System.out.println(cols.get(0).text());

                            }
                            if (cols.get(0).text().contains("Điểm trung bình tích lũy (hệ 4):")) {
                                System.out.println(cols.get(0).text());

                            }

                            if (cols.get(0).text().contains("Số tín chỉ đạt:")) {
                                System.out.println(cols.get(0).text());

                            }
                            if (cols.get(0).text().contains("Số tín chỉ tích lũy:")) {
                                System.out.println(cols.get(0).text());

                            }

                        }

                    }

                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }
    public static void main(String[] args) {
        test1 t = new test1();
        System.out.println(t.GettermScoreSummary("3118410488"));
    }
}


