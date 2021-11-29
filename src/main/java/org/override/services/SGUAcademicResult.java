package org.override.services;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.override.core.models.HyperStatus;
import org.override.models.ExampleModel;
import org.override.core.models.HyperEntity;
import org.override.core.models.HyperException;
import org.override.models.TermResult;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
public class SGUAcademicResult {
    public static final String URL = "http://thongtindaotao.sgu.edu.vn/default.aspx?page=nhapmasv&flag=XemDiemThi";
    String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36";

    public static final String TERM = "Học kỳ 1 - Năm học 2019-2020";
    final Gson gson = new Gson();

    public HyperEntity handleLookupSGUAcademicResult(Map<String, String> headers) {
        String clientMessage = headers.get("client_message");
        if (clientMessage == null) {
            return HyperEntity.badRequest(
                    new HyperException(HyperException.BAD_REQUEST, null, "field required: client_message")
            );
        }
        String info = lookupAcademicResult(clientMessage);

        return HyperEntity.ok(new ExampleModel(info));
    }

    private String lookupAcademicResult(String message) {
        StringBuilder result = new StringBuilder();
        try {
            Connection.Response response = Jsoup.connect(URL)
                    .method(Connection.Method.GET)
                    .execute();

            Document currentPage = response.parse();

            response = Jsoup.connect(URL)
                    .data("__EVENTTARGET", "")
                    .data("__EVENTARGUMENT", "")
                    .data("__VIEWSTATE", currentPage.getElementById("__VIEWSTATE").val())
                    .data("__VIEWSTATEGENERATOR", currentPage.getElementById("__VIEWSTATEGENERATOR").val())
                    .data("ctl00$ContentPlaceHolder1$ctl00$txtMaSV", message)
                    .data("ctl00$ContentPlaceHolder1$ctl00$btnOK", "OK")
                    .userAgent(USER_AGENT)
                    .timeout(0)
                    .followRedirects(true)
                    .cookies(response.cookies())
                    .method(Connection.Method.POST)
                    .execute();

            Document document = response.parse();
            List<Element> tables = document.getElementsByClass("view-table");
            if (tables.size() == 0) {
                return "NOT FOUND";
            }

            result.append(getMemberInfo(document.getElementById("id_form")));

            Element table = tables.get(0);
            Elements rows = table.select("tr");
            Iterator<Element> itr = rows.iterator();
            while (itr.hasNext()) {
                Element row = itr.next();
                if (row.text().equals(TERM)) {
                    Element currentRow;
                    while ((currentRow = itr.next()) != null) {
                        if (currentRow.classNames().contains("row-diemTK"))
                            break;
                        Elements cols = currentRow.select("td");
                        result.append(
                                String.format("%s | %s | %s \n",
                                        cols.get(1).text(),
                                        cols.get(2).text(),
                                        cols.get(9).text()
                                )
                        );

                    }

                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    private String getMemberInfo(Element el) {
        String name = el.getElementById("ctl00_ContentPlaceHolder1_ctl00_ucThongTinSV_lblTenSinhVien").text();
        String placeOfBorn = el.getElementById("ctl00_ContentPlaceHolder1_ctl00_ucThongTinSV_lblNoiSinh").text();
        String subject = el.getElementById("ctl00_ContentPlaceHolder1_ctl00_ucThongTinSV_lbNganh").text();
        String term = el.getElementById("ctl00_ContentPlaceHolder1_ctl00_ucThongTinSV_lblKhoaHoc").text();
        return String.format("%s | %s | %s | %s \n", name, placeOfBorn, subject, term);
    }
}
