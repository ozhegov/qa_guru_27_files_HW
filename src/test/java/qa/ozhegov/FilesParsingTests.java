package qa.ozhegov;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import qa.ozhegov.model.Recipe;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static java.lang.String.valueOf;

public class FilesParsingTests {

    private ClassLoader cl = FilesParsingTests.class.getClassLoader();

    @Test
    @DisplayName("Номер инвойса в PDF файле из архива соответствует ожидаемому")
    void pdfFileInZipArchiveContainsText() throws Exception {
        try (ZipInputStream zis = new ZipInputStream(
                cl.getResourceAsStream("sample_zip.zip")
        )) {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {

                if (entry.getName().equals("invoicesample.pdf")) {
                    PDF pdf = new PDF(zis);

                    Assertions.assertTrue(pdf.text.contains("Invoice Number: #20130304"));
                }
            }
        }
    }

    @DisplayName("Заголовок столбца XLS файла из архива имеет ожидаемое наименование")
    @CsvSource(value = {
            "1, First Name",
            "2, Last Name",
            "3, Gender",
            "4, Country",
            "5, Age",
            "6, Date",
            "7, Id",

    })
    @ParameterizedTest(name = "Столбец {0} имеет заголовок {1}")
    void xlsFileHeaderInZipArchiveContainsText(int cell, String cellName) throws Exception {
        try (ZipInputStream zis = new ZipInputStream(
                cl.getResourceAsStream("sample_zip.zip")
        )) {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {

                if (entry.getName().equals("file_example_XLS_50.xls")) {
                    XLS xls = new XLS(zis);
                    String actualValue = xls.excel.getSheetAt(0).getRow(0).getCell(cell).getStringCellValue();

                    Assertions.assertEquals(cellName, actualValue);
                }
            }
        }
    }

    @DisplayName("Строка CSV файла из архива имеет ожидаемое значение")
    @CsvSource(value = {
            "88, ' 2015', ' 54', Julianne Moore,  Still Alice",
            "89, ' 2016', ' 26',  Brie Larson,  Room"
    })
    @ParameterizedTest(name = "В строке {0} указан год {1}, возраст {2}, имя {3} и название фильма {4}")
    void csvFileRowsInZipArchiveContainsText(int row, String year, String age, String name, String movie) throws Exception {
        try (ZipInputStream zis = new ZipInputStream(
                cl.getResourceAsStream("sample_zip.zip")
        )) {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {

                if (entry.getName().equals("oscar_age_female.csv")) {

                    CSVReader csvReader = new CSVReader(new InputStreamReader(zis));

                        List<String[]> data = csvReader.readAll();

                        Assertions.assertArrayEquals(
                                new String[] {valueOf(row), year, age, name, movie},
                                data.get(row)
                        );
                }
            }
        }
    }

    @Test
    @DisplayName("Параметры JSON файла соответствуют ожидаемым")
    void jsonFileParamsHaveCorrectValues() throws Exception {
        try (Reader reader = new InputStreamReader(
                cl.getResourceAsStream("recipe.json")
        )) {

            ObjectMapper objectMapper = new ObjectMapper();
            Recipe actual = objectMapper.readValue(reader, Recipe.class);

            Assertions.assertEquals("0001", actual.getId());
            Assertions.assertEquals("donut", actual.getType());
            Assertions.assertEquals("Cake", actual.getName());
            Assertions.assertEquals(List.of("Regular", "Chocolate"), actual.getBatters().getType());
            Assertions.assertEquals(List.of("Big", "Medium", "Small"), actual.getBatters().getSize());
        }
    }

}
