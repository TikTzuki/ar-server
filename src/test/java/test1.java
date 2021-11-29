

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
    public static final String TERM = "Học kỳ 2 - Năm học 2019-2020";
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

    public String info(String mssv) {
        StringBuilder result = new StringBuilder();
        try {
            // StringBuilder result = new StringBuilder();

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
            //    String tenSV = loginPage.getElementById("ctl00_ContentPlaceHolder1_ctl00_ucThongTinSV_lblTenSinhVien").text();
            String gender = loginPage.getElementById("ctl00_ContentPlaceHolder1_ctl00_ucThongTinSV_lblPhai").text();
            String placeOfBirth = loginPage.getElementById("ctl00_ContentPlaceHolder1_ctl00_ucThongTinSV_lblNoiSinh").text();
            String Class = loginPage.getElementById("ctl00_ContentPlaceHolder1_ctl00_ucThongTinSV_lblLop").text();
            String majors = loginPage.getElementById("ctl00_ContentPlaceHolder1_ctl00_ucThongTinSV_lbNganh").text();


            String s = String.format("%s | %s | %s | %s | %s \n", getNgsinh(mssv), gender, placeOfBirth, Class, majors);
            result.append(s);
            System.out.println(s);


            List<Element> tables = loginPage.getElementsByClass("view-table");

            if (tables.size() == 0) {
                return "NOT FOUND";
            }

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

                    // System.out.println(lookupAcademicResult(mssv,term.get(i)));

                }


            }
        } catch (IOException e) {
            err.println(e);
        }
        return result.toString();
        // return null;
    }


    public String getall() {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File(".\\Export_ZDSDTDG1.xlsx"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        XSSFWorkbook wb = null;
        try {
            wb = new XSSFWorkbook(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        XSSFSheet sh = wb.getSheetAt(0);
        int rowcount = sh.getLastRowNum();
        for (int i = 0; i < rowcount; i++) {
            System.out.println(sh.getRow(i).getCell(0).getStringCellValue());
            info(sh.getRow(i).getCell(0).getStringCellValue());
        }


        return null;
    }

    private String lookupAcademicResult(String message) {
        StringBuilder result = new StringBuilder();
        try {
            Document document = Jsoup.connect(String.format(URL, message)).get();
         //   System.out.println(document);
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
                        Elements cols = currentRow.select("td");



                            if (currentRow.classNames().contains("row-diemTK")) {
                                System.out.println("fds");
                                System.out.println(cols.get(0).text());

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
    private String lookup(String message) {
        StringBuilder result = new StringBuilder();
        try {
            Document document = Jsoup.connect(String.format(URL, message)).get();
         //   System.out.println(document);
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
                            Elements cols = currentRow.select("td");
                            System.out.println(cols.get(0).text());

                        }

                    }break;


                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }
        public static void main (String[]args){
            test1 t = new test1();
         //   System.out.println(t.getall());
          // System.out.println(t.getNgsinh("3118410488"));
         //  System.out.println(t.info("3118410488"));
            System.out.println(t.lookupAcademicResult("3118410488"));

        }
    }


