<template>
  <div class="d-a-c lh30 ww100 pt16">
    <div class="pt30 tc" style="width: 80px">
      <p class="gray9">今日</p>
      <p class="gray9">昨日</p>
    </div>
    <div class="flex-1 tc">
      <p>成交额</p>
      <p class="f20 fb gray3">{{ dataModel.order.orderTotalPrice.today }}</p>
      <p class="gray">{{ dataModel.order.orderTotalPrice.yesterday }}</p>
    </div>
    <div class="flex-1 tc">
      <p>客单价</p>
      <p class="f20 fb gray3">{{ dataModel.order.orderPerPrice.today }}</p>
      <p class="gray">{{ dataModel.order.orderPerPrice.yesterday }}</p>
    </div>
    <div class="flex-1 tc">
      <p>付款订单</p>
      <p class="f20 fb gray3">{{ dataModel.order.orderTotal.today }}</p>
      <p class="gray">{{ dataModel.order.orderTotal.yesterday }}</p>
    </div>
    <div class="flex-1 tc">
      <p>付款人数</p>
      <p class="f20 fb gray3">{{ dataModel.order.orderUserTotal.today }}</p>
      <p class="gray">{{ dataModel.order.orderUserTotal.yesterday }}</p>
    </div>
    <div class="flex-1 tc">
      <p>成功退款金额</p>
      <p class="f20 fb gray3">{{ dataModel.order.orderRefundMoney.today }}</p>
      <p class="gray">{{ dataModel.order.orderRefundMoney.yesterday }}</p>
    </div>
    <div class="flex-1 tc">
      <p>退款订单</p>
      <p class="f20 fb gray3">{{ dataModel.order.orderRefundTotal.today }}</p>
      <p class="gray">{{ dataModel.order.orderRefundTotal.yesterday }}</p>
    </div>
  </div>
</template>
<script>
export default {
  components: {},
  data() {
    return {
      /*是否正在加载*/
      loading: true,
    };
  },
  inject: ["dataModel"],
  created() {},
  methods: {},
};
</script>

<style></style>
