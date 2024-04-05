import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataConverter {

    // Exel 데이터를 필드 candelMap 으로 변환하는 함수.
//    public void setExelDataToCandleMap(FileInputStream fis) throws IOException {
//        HSSFWorkbook workbook = new HSSFWorkbook(fis);
//        int sheetNum = workbook.getNumberOfSheets();
//        for (int i = 0; i < sheetNum; i++) {
//            HSSFSheet sheet = workbook.getSheetAt(i);
//            int rowNum = sheet.getPhysicalNumberOfRows(); // 해당 시트의 행의 개수
//            rowSize = rowNum;
//            List<CandleDTO> candleList = new ArrayList<>(Arrays.asList(new CandleDTO[rowSize - 1]));
//            for (int j = 1; j < rowNum; j++) {
//                HSSFRow row = sheet.getRow(j); // 각 행을 읽어온다
//                if (row == null) { // 현재 행에 아무 데이터도 없으면 빠져나옴.
//                    break;
//                }
//                CandleDTO dto = makeCandleDtoWithExelRow(row);
//                candleList.set(rowNum - j - 1, dto);
//            }
//            candleMap.put(sheet.getSheetName(), candleList);
//        }
//    }

    // 엑셀 row를 이용해서 CandleDTO 만드는 함수
    public CandleDTO makeCandleDtoWithExelRow(HSSFRow row) {
        CandleDTO candleDTO = new CandleDTO();
        candleDTO.setMarket(row.getCell(0).getStringCellValue());
        candleDTO.setTimeUTC(row.getCell(1).getStringCellValue());
        candleDTO.setTimeKST(row.getCell(2).getStringCellValue());
        candleDTO.setOpeningPrince(row.getCell(3).getNumericCellValue());
        candleDTO.setHighPrice(row.getCell(4).getNumericCellValue());
        candleDTO.setLowPrice(row.getCell(5).getNumericCellValue());
        candleDTO.setTradePrice(row.getCell(6).getNumericCellValue());
        candleDTO.setAccumulateTradePrice(row.getCell(7).getStringCellValue());
        candleDTO.setAccumulateTradeVolume(row.getCell(8).getStringCellValue());
        return candleDTO;
    }
}
