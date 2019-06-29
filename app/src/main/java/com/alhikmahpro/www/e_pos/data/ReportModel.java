package com.alhikmahpro.www.e_pos.data;

public class ReportModel {

    double Total,Discount,Refund,Net;

    String Employee;
    String Customer;
    String Date;
    String firstVisit;

    public String getFirstVisit() {
        return firstVisit;
    }

    public void setFirstVisit(String firstVisit) {
        this.firstVisit = firstVisit;
    }

    public String getLastVisit() {
        return lastVisit;
    }

    public void setLastVisit(String lastVisit) {
        this.lastVisit = lastVisit;
    }

    public int getTotalVisit() {
        return totalVisit;
    }

    public void setTotalVisit(int totalVisit) {
        this.totalVisit = totalVisit;
    }

    String lastVisit;
    int totalVisit;

    public double getTotal() {
        return Total;
    }

    public void setTotal(double total) {
        Total = total;
    }

    public double getDiscount() {
        return Discount;
    }

    public void setDiscount(double discount) {
        Discount = discount;
    }

    public double getRefund() {
        return Refund;
    }

    public void setRefund(double refund) {
        Refund = refund;
    }

    public double getNet() {
        return Net;
    }

    public void setNet(double net) {
        Net = net;
    }

    public String getEmployee() {
        return Employee;
    }

    public void setEmployee(String employee) {
        Employee = employee;
    }

    public String getCustomer() {
        return Customer;
    }

    public void setCustomer(String customer) {
        Customer = customer;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }



}
