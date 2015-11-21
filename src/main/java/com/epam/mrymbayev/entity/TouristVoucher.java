package com.epam.mrymbayev.entity;

import java.util.ArrayList;
import java.util.List;

public class TouristVoucher {
    List<Voucher> list;

    public TouristVoucher() {
        list = new ArrayList<>();
    }

    public TouristVoucher(List<Voucher> list) {
        this.list = list;
    }
}
