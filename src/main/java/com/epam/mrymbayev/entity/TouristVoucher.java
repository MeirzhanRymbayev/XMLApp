package com.epam.mrymbayev.entity;

import java.util.ArrayList;
import java.util.List;

public class TouristVoucher {
    List<Voucher> voucherList = new ArrayList<>();

    public TouristVoucher(){}

    public TouristVoucher(List<Voucher> voucherList) {
        this.voucherList = voucherList;
    }

    public boolean add(Voucher voucher){
        this.voucherList.add(voucher);
        return true;
    }


    @Override
    public String toString() {
        return voucherList.toString();
    }
}
