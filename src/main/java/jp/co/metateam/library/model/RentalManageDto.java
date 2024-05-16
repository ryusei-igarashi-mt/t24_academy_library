package jp.co.metateam.library.model;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jp.co.metateam.library.values.RentalStatus;
import lombok.Getter;
import lombok.Setter;

/**
 * 貸出管理DTO
 */
@Getter
@Setter
public class RentalManageDto {

    private Long id;

    @NotEmpty(message="在庫管理番号は必須です")
    private String stockId;

    @NotEmpty(message="社員番号は必須です")
    private String employeeId;

    @NotNull(message="貸出ステータスは必須です")
    private Integer status;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    @NotNull(message="貸出予定日は必須です")
    @FutureOrPresent(message="貸出予定日は現在以降の日付を選択してください")
    private Date expectedRentalOn;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    @NotNull(message="返却予定日は必須です")
    @FutureOrPresent(message="返却予定日は現在以降の日付を選択してください")
    private Date expectedReturnOn;

    private Timestamp rentaledAt;

    private Timestamp returnedAt;

    private Timestamp canceledAt;

    private Stock stock;

    private Account account;

    public Optional<String> isvalidStatus(Integer preStatus) {
        String er = "貸出ステータスは%sから%sには変更できません";

        if (preStatus == RentalStatus.RENT_WAIT.getValue() && this.status == RentalStatus.RETURNED.getValue()) {
            return Optional.of(String.format(er,"「貸出待ち」", "「返却済み」"));
        }   

        if (preStatus == RentalStatus.RENTAlING.getValue()) { 
            if (this.status == RentalStatus.RENT_WAIT.getValue()) {
                return Optional.of(String.format(er,"「貸出中」", "「貸出待ち」"));
            } else if (this.status == RentalStatus.CANCELED.getValue()) {
                return Optional.of(String.format(er,"「貸出中」", "「キャンセル」"));
            } 
        }

        if (preStatus == RentalStatus.RETURNED.getValue()) {
            if (this.status == RentalStatus.RENT_WAIT.getValue()) {
                return Optional.of(String.format(er,"「返却済み」", "「貸出待ち」"));
            } else if (this.status == RentalStatus.RENTAlING.getValue()) {
                return Optional.of(String.format(er,"「返却済み」", "「貸出中」"));
            } else if (this.status == RentalStatus.CANCELED.getValue()) {
                return Optional.of(String.format(er,"「返却済み」", "「キャンセル」"));
            }
        }

        if (preStatus == RentalStatus.CANCELED.getValue()) {
            if (this.status == RentalStatus.RENT_WAIT.getValue()) {
                return Optional.of(String.format(er,"「キャンセル」", "「貸出待ち」"));
            } else if (this.status == RentalStatus.RENTAlING.getValue()) {
                return Optional.of(String.format(er,"「キャンセル」", "「貸出中」"));
            } else if (this.status == RentalStatus.RETURNED.getValue()) {
                return Optional.of(String.format(er,"「キャンセル」", "「返却済み」"));
            }    
        }
           
        return Optional.empty();
    }
}
