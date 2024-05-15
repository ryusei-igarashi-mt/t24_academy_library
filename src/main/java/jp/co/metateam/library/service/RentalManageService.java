package jp.co.metateam.library.service;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.metateam.library.model.Account;
import jp.co.metateam.library.model.BookMst;
import jp.co.metateam.library.model.RentalManage;
import jp.co.metateam.library.model.RentalManageDto;
import jp.co.metateam.library.model.Stock;
import jp.co.metateam.library.model.StockDto;
import jp.co.metateam.library.repository.AccountRepository;
import jp.co.metateam.library.repository.RentalManageRepository;
import jp.co.metateam.library.repository.StockRepository;
import jp.co.metateam.library.values.RentalStatus;

@Service
public class RentalManageService {

    private final AccountRepository accountRepository;
    private final RentalManageRepository rentalManageRepository;
    private final StockRepository stockRepository;

     @Autowired
    public RentalManageService(
        AccountRepository accountRepository,
        RentalManageRepository rentalManageRepository,
        StockRepository stockRepository
    ) {
        this.accountRepository = accountRepository;
        this.rentalManageRepository = rentalManageRepository;
        this.stockRepository = stockRepository;
    }

    @Transactional
    public List <RentalManage> findAll() {
        List <RentalManage> rentalManageList = this.rentalManageRepository.findAll();

        return rentalManageList;
    }

    @Transactional
    public RentalManage findById(Long id) {
        return this.rentalManageRepository.findById(id).orElse(null);
    }

    @Transactional 
    public void save(RentalManageDto rentalManageDto) throws Exception {
        try {
            Account account = this.accountRepository.findByEmployeeId(rentalManageDto.getEmployeeId()).orElse(null);
            if (account == null) {
                throw new Exception("Account not found.");
            }

            Stock stock = this.stockRepository.findById(rentalManageDto.getStockId()).orElse(null);
            if (stock == null) {
                throw new Exception("Stock not found.");
            }

            RentalManage rentalManage = new RentalManage();
            rentalManage = setRentalStatusDate(rentalManage, rentalManageDto.getStatus());

            rentalManage.setAccount(account);
            rentalManage.setExpectedRentalOn(rentalManageDto.getExpectedRentalOn());
            rentalManage.setExpectedReturnOn(rentalManageDto.getExpectedReturnOn());
            rentalManage.setStatus(rentalManageDto.getStatus());
            rentalManage.setStock(stock);

            // データベースへの保存
            this.rentalManageRepository.save(rentalManage);
        } catch (Exception e) {
            throw e;
        }
    }
    @Transactional //エラーが発生した場合はロールバック
    //updateメソッドは、貸出情報の更新と場合によって例外処理
    public void update(Long id, RentalManageDto rentalManageDto) throws Exception {
        try {
            //リポジトリを使用してDtoの社員番号と同じAccount情報を取得
            Account account = this.accountRepository.findByEmployeeId(rentalManageDto.getEmployeeId()).orElse(null);
            //アカウントが空の場合、例外処理へ
            if (account == null) {
                throw new Exception("Account not found.");
            }
            //リポジトリを使用してDtoの在庫管理番号と同じStock情報を取得
            Stock stock = this.stockRepository.findById(rentalManageDto.getStockId()).orElse(null);;
            if (stock == null) {
                throw new Exception("Stock not found.");
            }
            //指定されたIDの貸出管理情報を取得
            RentalManage rentalManage = findById(id);
            if (rentalManage == null) {
                throw new Exception("rentalManage record not found.");
            }
            //DtoのIDを、rentalManageのIDに設定する
            rentalManageDto.setId(rentalManage.getId());
            //貸出管理レコード更新処理
            rentalManage.setAccount(account);
            rentalManage.setExpectedRentalOn(rentalManageDto.getExpectedRentalOn());
            rentalManage.setExpectedReturnOn(rentalManageDto.getExpectedReturnOn());
            rentalManage.setStatus(rentalManageDto.getStatus());
            rentalManage.setStock(stock);


            // データベースへの保存
            this.rentalManageRepository.save(rentalManage);
        } catch (Exception e) {
            throw e;
        }
    }
    //貸出ステータスに基づいて日時を設定するメソッド
    private RentalManage setRentalStatusDate(RentalManage rentalManage, Integer status) {
        //設定する日時は現在の日時ですよ
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        //貸出中の場合は、貸出日時をrentalManageに設定
        if (status == RentalStatus.RENTAlING.getValue()) {
            rentalManage.setRentaledAt(timestamp);
        //返却済みの場合は、返却日時をrentalManageに設定
        } else if (status == RentalStatus.RETURNED.getValue()) {
            rentalManage.setReturnedAt(timestamp);
        //キャンセルの場合は、キャンセル日時をrentalManageに設定
        } else if (status == RentalStatus.CANCELED.getValue()) {
            rentalManage.setCanceledAt(timestamp);
        }
        //更新された情報をrentalManageに返す
        return rentalManage;
    }
}
