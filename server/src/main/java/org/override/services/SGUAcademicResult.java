package org.override.services;

import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.override.models.HyperEntity;
import org.override.models.HyperException;
import org.override.utils.ErrorCodes;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
public class SGUAcademicResult {
    public static final String URL = "http://thongtindaotao.sgu.edu.vn/Default.aspx?page=xemdiemthi&id=%s";
    public static final String TERM = "Học kỳ 1 - Năm học 2019-2020";

    public HyperEntity<Object> handleLookupSGUAcademicResult(Map<String, String> headers) {
        String clientMessage = headers.get("client_message");
        if (clientMessage == null) {
            return HyperEntity.badRequest(
                    new HyperException(ErrorCodes.BAD_REQUEST, null, "field required: client_message")
            );
        }
        String info = lookupAcademicResult(clientMessage);
        return HyperEntity.ok(info);
    }

    private String lookupAcademicResult(String message) {
        StringBuilder result = new StringBuilder();
        try {
            Document document = Jsoup.connect(String.format(URL, message)).get();
            System.out.println(document);
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
