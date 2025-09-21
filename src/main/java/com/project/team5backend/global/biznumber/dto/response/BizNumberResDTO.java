package com.project.team5backend.global.biznumber.dto.response;

import java.util.List;

public class BizNumberResDTO {
    public record BizInfo(
            String status_code,
            List<InfoItem> data
    ) {
        public record InfoItem(
                // 사업자등록번호
                String b_no,

                // 납세자상태(명칭):
                // 01: 계속사업자,
                // 02: 휴업자,
                // 03: 폐업자
                String b_stt,

                // 납세자상태(코드):
                // 01: 계속사업자,
                // 02: 휴업자,
                // 03: 폐업자
                String b_stt_cd,

                // 과세유형메세지(명칭):
                // 01:부가가치세 일반과세자,
                // 02:부가가치세 간이과세자,
                // 03:부가가치세 과세특례자,
                // 04:부가가치세 면세사업자,
                // 05:수익사업을 영위하지 않는 비영리법인이거나 고유번호가 부여된 단체,국가기관 등,
                // 06:고유번호가 부여된 단체,
                // 07:부가가치세 간이과세자(세금계산서 발급사업자),
                // * 등록되지 않았거나 삭제된 경우: "국세청에 등록되지 않은 사업자등록번호입니다"
                String tax_type,

                // 과세유형메세지(코드):
                // 01:부가가치세 일반과세자,
                // 02:부가가치세 간이과세자,
                // 03:부가가치세 과세특례자,
                // 04:부가가치세 면세사업자,
                // 05:수익사업을 영위하지 않는 비영리법인이거나 고유번호가 부여된 단체,국가기관 등,
                // 06:고유번호가 부여된 단체,
                // 07:부가가치세 간이과세자(세금계산서 발급사업자)
                String tax_type_cd,

                // 폐업일 (YYYYMMDD 포맷)
                String end_dt,

                //단위과세전환폐업여부(Y,N)
                String utcc_yn,

                // 최근과세유형전환일자 (YYYYMMDD 포맷)
                String tax_type_change_dt,

                // 세금계산서적용일자 (YYYYMMDD 포맷)
                String invoice_apply_dt,

                // 직전과세유형메세지(명칭):
                // 01:부가가치세 일반과세자,
                // 02:부가가치세 간이과세자,
                // 07:부가가치세 간이과세자(세금계산서 발급사업자),
                // 99:해당없음
                String rbf_tax_type,

                // 직전과세유형메세지(코드):
                // 01:부가가치세 일반과세자,
                // 02:부가가치세 간이과세자,
                // 07:부가가치세 간이과세자(세금계산서 발급사업자),
                // 99:해당없음
                String rbf_tax_type_cd
        ) {
        }
        public InfoItem getInfoItem() {
            return this.data.get(0);
        }
    }
}
