package com.redhat.demo.billing;

public class Call {
  private String sender;
  private String receiver;
  private Integer mins;
  private Double charge;

  public Integer getMins() {
    return mins;
  }

  public void setMins(Integer mins) {
    this.mins = mins;
  }

  public String getReceiver() {
    return receiver;
  }

  public void setReceiver(String receiver) {
    this.receiver = receiver;
  }

  public String getSender() {
    return sender;
  }

  public void setSender(String sender) {
    this.sender = sender;
  }

  public Double getCharge() {
    return charge;
  }

  public void setCharge(Double charge) {
    this.charge = charge;
  }

  public boolean sameAreaCode() {
    String senderAreaCode = sender.substring(0, 3);
    String receiverAreaCode = receiver.substring(0, 3);

    if(senderAreaCode != null && senderAreaCode.equals(receiverAreaCode))
      return true;
    return false;
  }

  public String toString() {

    return "Call sender:[" + sender + "] receiver:[" + receiver + "] call last:[" + mins + "] mins charge:[" + charge
        + "]";
  }

}
