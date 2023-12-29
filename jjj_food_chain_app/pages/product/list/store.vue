<template>
	<view class="container" :data-theme="theme()" :class="theme() || ''">
		<view class="main">
			<view class="nav">
				<view class="d-b-c top-status" v-if="tableId != 0 && table_detail">
					<view>当前桌号:{{ table_detail.table_no }}</view>
					<view class="flex-1 tc f28 dominant" v-if="orderId"
						@click="gotoPage('/pages/order/store_order?orderId=' + orderId + '&pnum=' + num)">查看订单</view>
					<view class="d-e-c" @click="goBack">
						人数:{{ num }}人
						<u-icon name="edit-pen-fill" size="19"></u-icon>
					</view>
				</view>
				<view class="header">
					<view class="left">
						<view class="store-name">
							<text class="fb">{{ supplier.name }}</text>
						</view>
					</view>
					<view class="right" v-if="tableId == 0">
						<template v-for="(item, index) in store_set" :key="item">
							<view class="takeout" v-if="item == '30'" :class="orderType == 'takeout' ? 'active' : ''"
								@tap="takout">
								<text>打包</text>
							</view>
						</template>
						<template v-for="(item, index) in store_set" :key="item">
							<view class="dinein" v-if="item == '40'" :class="orderType == 'takein' ? 'active' : ''"
								@tap="takein">
								<text>店内</text>
							</view>
						</template>
					</view>
				</view>
				<view class="reduce_list" v-if="tableId == 0">
					<view v-for="(item, index) in reduceList" :key="index" class="reduce_item fb">{{ item.activeName }}
					</view>
				</view>
			</view>
			<view class="content">
				<scroll-view class="menus" :style="'height:' + scrollviewHigh + 'px;'"
					:scroll-into-view="menuScrollIntoView" :scroll-with-animation="true" :scroll-animation-duration="1"
					scroll-y>
					<view class="wrapper">
						<template v-for="(item, index) in goods_list" :key="index">
							<view class="menu" :id="`menu-${item.categoryId}`"
								:class="{ current: item.categoryId === currentCateId }"
								v-if="item.products.length != 0" @tap="handleMenuTap(item.categoryId)">
								<image v-if="item.images" class="f-s-0 mr10" :src="item.images.filePath"
									mode="aspectFill" style="width: 30rpx;height: 30rpx;"></image>
								<text>{{ item.name }}</text>
								<view class="dot" v-if="menuCartNum(item.categoryId)">
									{{ menuCartNum(item.categoryId) }}
								</view>
							</view>
						</template>
					</view>
				</scroll-view>
				<!-- goods list begin -->
				<scroll-view class="goods pr" :style="'height:' + scrollviewHigh + 'px;'" :scroll-with-animation="true"
					:scroll-animation-duration="1" scroll-y :scroll-top="cateScrollTop" @scroll="handleGoodsScroll">
					<view class="wrapper">
						<view class="list">
							<!-- category begin -->
							<template v-for="(item, index) in goods_list" :key="index">
								<view class="category" v-if="item.products.length != 0"
									:id="`cate-${item.categoryId}`">
									<view class="title">
										<text>{{ item.name }}</text>
									</view>
									<view class="items">
										<!-- 商品 begin -->
										<view class="good" @click="gotoDetail(good)"
											v-for="(good, key) in item.products" :key="key">
											<image :src="good.productImage" class="image"></image>
											<view class="right">
												<text class="name">{{ good.productName }}</text>
												<text class="tips text-ellipsis">{{ good.sellingPoint }}</text>
												<view class="price_and_action">
													<view>
														<text style="color: #FF5800;font-size: 22rpx;">￥</text>
														<text class="price">{{ good.productPrice * 1 }}</text>
														<text class="linprice"
															v-if="good.productPrice * 1 != good.linePrice * 1">￥{{ good.linePrice * 1 }}</text>
													</view>
													<view class="btn-group" v-if="good.specType == 20">
														<button type="primary" class="btn property_btn"
															hover-class="none" size="min"
															@tap.stop="gotoDetail(good)">选规格</button>
														<view class="dot" v-if="good.cartNum != 0">{{ good.cartNum }}
														</view>
													</view>
													<view class="btn-group" v-else>
														<button v-if="good.cartNum != 0" plain class="btn reduce_btn"
															size="min" hover-class="none" @tap.stop="reduceFunc(good)">
															<view class="iconfont  icon icon-jian iconsami-select">
															</view>
														</button>
														<view class="number" v-if="good.cartNum != 0">
															{{ good.cartNum }}
														</view>
														<button class="btn add_btn" size="min" hover-class="none"
															@tap.stop="addCart(good)">
															<view class="iconfont  icon icon-jia iconadd-select"></view>
														</button>
													</view>
												</view>
											</view>
										</view>
										<!-- 商品 end -->
									</view>
								</view>
							</template>

							<!-- category end -->
						</view>
					</view>
				</scroll-view>
				<!-- goods list end -->
			</view>
			<!-- content end -->
			<!-- 购物车栏 begin -->
			<view class="cart-box" v-if="cartTotalNum > 0">
				<view class="mark">
					<image src="/static/images/menu/cart.png" class="cart-img" @tap="openCartPopup"></image>
					<view class="tag">{{ cartTotalNum }}</view>
				</view>
				<view class="price">
					<view>
						<text class="f22">￥</text>
						{{ totalPrice }}
						<text class="gray9 f22 text-d-line fn ml10">{{ linePrice }}</text>
					</view>
					<view class="gray9 f22 fn" v-if="totalBagPrice != 0">
						<text class="">包含包装费￥</text>
						{{ totalBagPrice }}
					</view>
					<view class="gray9 f22 fn" v-else><text>免包装费</text></view>
				</view>
				<template v-if="tableId != 0">
					<button v-if="orderId == 0" type="primary" class="pay-btn" @tap="toPay"><text>下单</text></button>
					<button v-else type="primary" class="pay-btn" @tap="addpay">去加餐</button>
				</template>
				<template v-else>
					<button v-if="addorderId == 0" type="primary" class="pay-btn"
						@tap="toPay"><text>去结算</text></button>
					<button v-else type="primary" class="pay-btn" @tap="addpay">去加餐</button>
				</template>
			</view>
			<!-- 购物车栏 end -->
		</view>
		<!-- 购物车详情popup -->
		<popup-layer direction="top" :show-pop="cartPopupVisible" class="cart-popup" v-if="cartTotalNum > 0">
			<template #content>
				<view class="cart-popup">
					<view class="d-b-c top-title" v-if="reduce && tableId == 0">
						<view class="mj d-c-c">
							<view v-if="reduce.now">
								已
								<text class="">{{ reduce.now.activeName }}</text>
								<text v-if="reduce.next">，</text>
							</view>
							<view v-if="reduce.next">
								<text class="">{{ reduce.next.activeName }}</text>
								还差
								<text class="">{{ reduceDiffValue }}</text>
								{{ reduce.next.fullType == 1 ? '元' : '件' }}
							</view>
						</view>
					</view>
					<view class="top d-b-c">
						<view>已选商品</view>
						<view @tap="handleCartClear">清空购物车</view>
					</view>
					<scroll-view class="cart-list" scroll-y>
						<view class="wrapper">
							<template v-for="(item, index) in cart_list" :key="index">
								<view class="item" v-if="item.productNum > 0">
									<view class="d-s-c">
										<view class="cart-image">
											<image style="" :src="item.productImage" mode="aspectFill"></image>
										</view>
										<view class="left">
											<view>
												<view class="name">{{ item.productName }}</view>
												<view class="gray9">{{ item.describ }}</view>
											</view>
											<view class="center">
												<text class="fb">￥{{ item.price }}</text>
												<template
													v-if=" item.bagPrice > 0 ">
													<text class="f24 gray9">(包装费：￥{{ item.bagPrice }})</text>
												</template>
											</view>
										</view>
									</view>
									<view class="right">
										<button plain size="min" class="btn theme-borderbtn" hover-class="none"
											@tap="cartReduce(item)">
											<view class="iconfont icon icon-jian iconsami-select"></view>
										</button>
										<view class="number">{{ item.productNum }}</view>
										<button class="btn theme-btn" size="min" hover-class="none"
											@tap="cartAdd(item)">
											<view class="iconfont icon icon-jia iconadd-select"></view>
										</button>
									</view>
								</view>
							</template>
						</view>
					</scroll-view>
				</view>
			</template>
		</popup-layer>
		<!-- 购物车详情popup -->
	</view>

	<view class="loading" v-if="loading">
		<image src="/static/images/loading.gif"></image>
	</view>
</template>

<script>
	import utils from '@/common/utils.js';
	import modal from '@/components/modal/modal';
	import popupLayer from '@/components/popup-layer/popup-layer';
	export default {
		components: {
			modal,
			popupLayer,
		},
		data() {
			return {
				isLoading: true,
				goods: [], //所有商品
				supplier: {
					name: ''
				},
				ads: [],
				loading: true,
				currentCateId: 6905, //默认分类
				cateScrollTop: 0,
				menuScrollIntoView: '',
				cart: [], //购物车
				goodDetailModalVisible: false, //是否饮品详情模态框
				good: {}, //当前饮品
				category: {}, //当前饮品所在分类
				cartPopupVisible: false,
				sizeCalcState: false,
				listData: [],
				goods_list: [],
				productModel: {},
				clock: false,
				cartTotalNum: 0,
				totalPrice: 0,
				totalBagPrice: 0,
				cart_list: [],
				orderType: 'takein',
				takeout_address: {},
				phoneHeight: 0,
				/*可滚动视图区域高度*/
				scrollviewHigh: 0,
				delivery_time: ['00:00', '00:00'],
				store_time: ['00:00', '00:00'],
				officeTime: {
					now: 0,
					delivery_start: 0,
					delivery_end: 0,
					store_start: 0,
					store_end: 0
				},
				addclock: false,
				longitude: 0,
				latitude: 0,
				bagType: 1,
				shopSupplierId: 0,
				/* 10配送20自提30店内 */
				dinnerType: 30,
				cartType: 1,
				tableId: 0,
				orderId: 0,
				addorderId: 0,
				reduceList: [],
				reduce: {},
				reduceDiffValue: 0,
				linePrice: 0,
				isFirst: true,
				store_set: [],
				num: 1,
				table_detail: null,
				options: {},
				settle_type: 0
			};
		},
		onLoad(e) {
			let self = this;
			let scene = utils.getSceneData(e);
			self.options = e;
			self.shopSupplierId = e.shopSupplierId ? e.shopSupplierId : scene.sid;
			self.tableId = e.tableId ? e.tableId : scene.tid;
			if (!self.tableId) {
				self.tableId = 0;
			}
			if (!self.shopSupplierId) {
				self.shopSupplierId = uni.getStorageSync('selectedId') ? uni.getStorageSync('selectedId') : 0;
			}
			self.num = e.num || 0;
			self.addorderId = e.orderId || 0;
			uni.setNavigationBarTitle({
				title: self.tableId == 0 ? '快餐模式' : '堂食点餐'
			});
		},
		onShow() {
			let self = this;
			self.init();
		},
		computed: {
			menuCartNum() {
				return id =>
					this.cart.reduce((acc, cur) => {
						if (cur.cateId === id) {
							return (acc += cur.number);
						}
						return acc;
					}, 0);
			}
		},
		methods: {
			goBack() {
				// #ifndef H5
				uni.navigateBack({
					delta: 1
				});
				// #endif
				// #ifdef H5
				history.go(-1);
				// #endif
			},
			scrollInit() {
				let self = this;
				uni.getSystemInfo({
					success(res) {
						self.phoneHeight = res.windowHeight;
						// 计算组件的高度
						let view = uni.createSelectorQuery().select('.nav');
						view.boundingClientRect(data => {
							let h = self.phoneHeight - data.height;
							self.scrollviewHigh = h;
						}).exec();
					}
				});
			},
			takout() {
				if (this.orderType == 'takeout') return;
				this.orderType = 'takeout';
				this.dinnerType = 10;
				this.init();
			},
			takein() {
				if (this.orderType == 'takein') return;
				this.orderType = 'takein';
				this.dinnerType = 20;
				this.init();
			},
			init() {
				//页面初始化
				this.addclock = false;
				this.category = {};
				this.good = {};
				this.goodDetailModalVisible = false;
				this.clock = false;
				this.loading = true;
				this.isLoading = true;
				this.getCategory();
				this.sizeCalcState = false;
			},
			/* 获取商品类型 */
			getCategory() {
				let self = this;
				this.sizeCalcState = false;
				uni.showLoading({
					title: '加载中'
				});
				self._postBody(
					'product/category/index', {
						/* 0外卖分类，1堂食分类 */
						type: 1,
						shopSupplierId: self.shopSupplierId,
						longitude: 0,
						latitude: 0,
						tableId: self.tableId,
						/* 30 打包 40店内 */
						delivery: self.orderType == 'takeout' ? 30 : 40,
						orderType: self.tableId == 0 ? 1 : 2
					},
					function(res) {
						self.settle_type = res.data.supplier.settleType;
						self.reduceList = res.data.reduceList;
						self.goods_list = res.data.list;
						self.supplier = res.data.supplier;
						self.store_set = res.data.supplier.storeSetList;
						if (self.tableId != 0) {
							self.orderType = 'takein';
						} else if (self.isFirst && self.orderType == '') {
							if (self.store_set.indexOf(30) != -1) {
								self.orderType = 'takeout';
							} else {
								self.orderType = 'takein';
							}
							self.isFirst = false;
						}
						self.shopSupplierId = res.data.supplier.shopSupplierId;
						self.bagType = res.data.supplier.storebagType;
						self.loading = false;
						self.isLoading = false;
						self.$nextTick(function() {
							self.scrollInit();
						});
						if (self.getUserId()) {
							self.getCart();
						}
						uni.hideLoading();
					},
					function() {
						self.gotoPage('/pages/index/index');
					}
				);
			},
			/*获取数据*/
			getProduct(item) {
				let self = this;
				if (self.clock == true) {
					return;
				}
				self.clock = true;
				self.good = item;
				let productId = item.productId;
				self.detail = item;
				self.showGoodDetailModal();
			},
			reCart(res) {
				let self = this;
				if(res.data.cartInfo!=null){
					self.cartTotalNum = res.data.cartInfo.cartTotalNum;
				self.totalPrice = res.data.cartInfo.totalPayPrice;
				self.linePrice = res.data.cartInfo.totalLineMoney;
				self.totalBagPrice = res.data.cartInfo.totalBagPrice;
				self.reduce = res.data.cartInfo.reduce;
				self.reduceDiffValue = res.data.cartInfo.reduceDiffValue;
				}
				
			},
			addCart(goods) {
				let self = this;
				if (self.addclock) {
					return;
				}
				if (goods.limitNum != 0 && goods.limitNum <= goods.cartNum) {
					uni.showToast({
						icon: 'none',
						title: '超过限购数量'
					});
					return;
				}
				if (goods.productStock <= 0 || goods.productStock <= goods.cartNum) {
					uni.showToast({
						icon: 'none',
						title: '没有更多库存了'
					});
					return;
				}
				let params = {
					productId: goods.productId,
					productNum: 1,
					productSkuId: goods.skuList[0].productSkuId,
					attr: '',
					feed: '',
					describ: '',
					price: goods.skuList[0].productPrice,//商品价格
					bagPrice: goods.skuList[0].bagPrice,
					shopSupplierId: self.shopSupplierId,
					tableId: self.tableId,
					cartType: 1,
					dinnerType: self.dinnerType,
					productPrice: goods.skuList[0].linePrice,//划线价格
					delivery: self.orderType == 'takeout' ? 30 : 40
				};
				self.addclock = true;
				let url = 'order/cart/add';
				self._postBody(
					url,
					params,
					function(res) {
						let num = 1;
						self.reCart(res);
						if (goods.cartNum) {
							num = goods.cartNum + 1;
						}
						self.goods_list.forEach((item, index) => {
							item.products.forEach((product, product_index) => {
								if (product.productId == goods.productId) {
									self.$set(product, 'cartNum', product.cartNum + 1);
								}
							});
						});
						self.addclock = false;
						self.getCategory();
					},
					function(err) {
						self.addclock = false;
					}
				);
			},
			reduceFunc(goods) {
				let self = this;
				if (self.addclock || goods.cartNum <= 0) {
					return;
				}
				let productId = goods.productId;
				self.addclock = true;
				let url = 'order/cart/productSub';
				self._postBody(
					url, {
						productId: productId,
						type: 'down',
						cartType: 1,
						shopSupplierId: self.shopSupplierId,
						tableId: self.tableId,
						dinnerType: self.dinnerType,
						delivery: self.orderType == 'takeout' ? 30 : 40
					},
					function(res) {
						self.reCart(res);
						self.goods_list.forEach((item, index) => {
							item.products.forEach((product, product_index) => {
								if (product.productId == goods.productId) {
									self.$set(product, 'cartNum', product.cartNum - 1);
								}
							});
						});
						self.addclock = false;
						self.getCategory();
					},
					function() {
						self.addclock = false;
					}
				);
			},
			getCart() {
				let id = uni.getStorageSync('user_id');
				if (!id) {
					return;
				}
				let self = this;
				uni.showLoading({
					title: '加载中',
					mask: true
				});
				self.getFastCart();
			},
			getFastCart() {
				let self = this;
				self._postBody(
					'order/cart/lists', {
						shopSupplierId: self.shopSupplierId,
						cartType: 1,
						delivery: self.orderType == 'takeout' ? 30 : 40
					},
					function(res) {
						uni.hideLoading();
						self.isLoading = true;
						self.reCart(res);
						self.cart_list = res.data.productList;
					}
				);
			},
			/* 购物车商品添加 */
			cartAdd(goods) {
				let self = this;
				if (self.addclock) {
					return;
				}
				self.addclock = true;
				let num = goods.productNum + 1;
				let productId = goods.productId;
				let totalNum = 1;
				let url = 'order/cart/sub';
				self._postBody(
					url, {
						productId: productId,
						totalNum: totalNum,
						cartId: goods.cartId,
						type: 'up',
						cartType: 1,
						dinnerType: self.dinnerType,
						shopSupplierId: self.shopSupplierId,
						tableId: self.tableId,
						delivery: self.orderType == 'takeout' ? 30 : 40
					},
					function(res) {
						self.addclock = false;
						self.reCart(res);
						self.goods_list.forEach((item, index) => {
							item.products.forEach((product, product_index) => {
								if (product.productId == goods.productId) {
									self.$set(product, 'cartNum', product.cartNum + 1);
								}
							});
						});
						self.$set(goods, 'productNum', num);
						self.$set(goods, 'totalNum', goods.totalNum + 1);
						self.addclock = false;
						self.getCategory();
					},
					function() {
						self.addclock = false;
					}
				);
			},
			/* 购物车商品减少 */
			cartReduce(goods) {
				let self = this;
				if (self.addclock) {
					return;
				}
				self.addclock = true;
				let productId = goods.productId;
				let num = goods.productNum;
				let url = 'order/cart/sub';
				self._postBody(
					url, {
						productId: productId,
						totalNum: 1,
						cartId: goods.cartId,
						type: 'down',
						cartType: 1,
						dinnerType: self.dinnerType,
						shopSupplierId: self.shopSupplierId,
						tableId: self.tableId,
						delivery: self.orderType == 'takeout' ? 30 : 40
					},
					function(res) {
						num--;
						self.reCart(res);
						self.goods_list.forEach((item, index) => {
							item.products.forEach((product, product_index) => {
								if (product.productId == goods.productId) {
									self.$set(product, 'cartNum', product.cartNum - 1);
								}
							});
						});
						self.$set(goods, 'productNum', num);
						self.$set(goods, 'totalNum', goods.totalNum - 1);
						self.addclock = false;
						self.getCategory();
					},
					function() {
						self.addclock = false;
					}
				);
			},
			//清空购物车
			handleCartClear() {
				let self = this;
				uni.showModal({
					title: '提示',
					content: '确定清空购物车么',
					success(res) {
						if (res.confirm) {
							self.clearCart();
						} else if (res.cancel) {
							console.log('用户点击取消');
						}
					}
				});
			},
			/*  清空 */
			clearCart() {
				let self = this;
				let url = 'order/cart/delete';
				let params = {
					shopSupplierId: self.shopSupplierId,
					cartType: 1
				};
				self._postBody(url, params, function(res) {
					self.cartPopupVisible = false;
					self.cart_list = [];
					self.init();
					self.Refresh();
				});
			},
			        Refresh(){
        window.location.reload();
      },
			/* 提交 */
			toPay() {
				let self = this;
				self.fastToPay();
			},
			/* 快餐结算 */
			fastToPay() {
				let self = this;
				uni.showLoading({
					title: '加载中'
				});
				let delivery = self.orderType == 'takeout' ? 30 : 40;
				self._postBody(
					'order/cart/lists', {
						shopSupplierId: self.shopSupplierId,
						cartType: 1,
						delivery: delivery
					},
					function(res) {
						self.reCart(res);
						self.cart_list = res.data.productList;
						let arrIds = [];
						self.cart_list.forEach(item => {
							arrIds.push(item.cartId);
						});
						if (arrIds.length == 0) {
							uni.showToast({
								title: '请选择商品',
								icon: 'none'
							});
							return false;
						}
						uni.hideLoading();
						uni.navigateTo({
							url: '/pages/order/confirm-order?orderType=cart&cartIds=' +
								arrIds.join(',') +
								'&delivery=' +
								delivery +
								'&shopSupplierId=' +
								self.shopSupplierId +
								'&cartType=1' +
								'&dinnerType=30&tableId=' +
								self.tableId
						});
					}
				);
			},
			submitOrder(arrIds) {
				let self = this;
				let params = {
					delivery: self.orderType == 'takeout' ? 30 : 40,
					linkman: '',
					phone: '',
					remark: '',
					mealtime: '',
					meal_num: self.num,
					cartIds: arrIds.join(',') || 0,
					dinnerType: self.dinnerType,
					shopSupplierId: self.shopSupplierId || 0,
					orderType: 1,
					tableId: self.tableId
				};
				self._post(
					'order/order/tableBuy',
					params,
					function(res) {
						self.orderId = res.data.orderId;
						self.gotoPage('/pages/order/store_order?orderId=' + self.orderId + '&pnum=' + self.num);
					},
					function(err) {
						self.init();
					}
				);
			},
			/* 加餐 */
			addpay() {
				let self = this;
				uni.showLoading({
					title: '加载中'
				});
				self.addpayFunc();
			},
			/* 快餐模式加餐 */
			addpayFunc() {
				let self = this;
				self._postBody(
					'order/cart/lists', {
						shopSupplierId: self.shopSupplierId,
						cartType: 1,
						delivery: self.orderType == 'takeout' ? 30 : 40
					},
					function(res) {
						self.cartTotalNum = res.data.cartInfo.cartTotalNum;
						self.totalPrice = res.data.cartInfo.totalPrice;
						self.cart_list = res.data.productList;
						let arrIds = [];
						self.cart_list.forEach(item => {
							arrIds.push(item.cartId);
						});
						if (arrIds.length == 0) {
							uni.showToast({
								title: '请选择商品',
								icon: 'none'
							});
							return false;
						}
						uni.hideLoading();
						let url =
							'/pages/order/addorder?orderType=cart&cartIds=' +
							arrIds.join(',') +
							'&delivery=40&shopSupplierId=' +
							self.shopSupplierId +
							'&cartType=1' +
							'&dinnerType=30&tableId=' +
							self.tableId +
							'&orderId=' +
							self.addorderId;
						self.gotoPage(url);
					}
				);
			},
			/* 堂食模式加餐 */
			tableAddOrder(arrIds) {
				let self = this;
				let params = {
					delivery: self.orderType == 'takeout' ? 30 : 40,
					linkman: '',
					phone: '',
					remark: '',
					mealtime: '',
					meal_num: self.num,
					cartIds: arrIds.join(',') || 0,
					dinnerType: self.dinnerType,
					shopSupplierId: self.shopSupplierId || 0,
					orderType: 1,
					tableId: self.tableId,
					orderId: self.orderId
				};
				self._post(
					'order/order/tableAddMeal',
					params,
					function(res) {
						self.orderId = res.data.orderId;
						self.gotoPage('/pages/order/store_order?orderId=' + self.orderId + '&pnum=' + self.num);
					},
					function(err) {
						self.init();
					}
				);
			},
			gotoDetail(e) {
				let self = this;
				let delivery = self.orderType == 'takeout' ? 30 : 40;
				/* 快餐模式加餐 */
				let url =
					'/pages/product/detail/detail?productId=' +
					e.productId +
					'&delivery=' +
					delivery +
					'&bagType=' +
					this.bagType +
					'&dinnerType=' +
					this.dinnerType +
					'&cartType=' +
					this.cartType;
				/* 堂食模式加餐 */
				if (self.tableId != 0) {
					url = url + '&tableId=' + this.tableId;
				}
				uni.navigateTo({
					url: url
				});
			},
			//点击菜单项事件
			handleMenuTap(id) {
				if (!this.sizeCalcState) {
					this.calcSize();
				}
				this.currentCateId = id;
				this.$nextTick(() => (this.cateScrollTop = this.goods_list.find(item => item.categoryId == id).top));
			},
			//商品列表滚动事件
			handleGoodsScroll({
				detail
			}) {
				if (!this.sizeCalcState) {
					this.calcSize();
				}
				const {
					scrollTop
				} = detail;
				let tabs = this.goods_list.filter(item => item.top <= scrollTop).reverse();
				if (tabs.length > 0) {
					this.currentCateId = tabs[0].categoryId;
				}
			},
			calcSize() {
				let h = 10;
				this.goods_list.forEach(item => {
					let view = uni.createSelectorQuery().select(`#cate-${item.categoryId}`);
					view.fields({
							size: true
						},
						data => {
							item.top = h;
							if (data != null) {
								h += data.height;
							}
							item.bottom = h;
						}
					).exec();
				});
				this.sizeCalcState = true;
			},
			//打开商品详情模态框
			showGoodDetailModal() {
				this.detail.skuList.forEach((item, index) => {
					item.checked = false;
				});
				let obj = {
					specData: this.detail.skuList,
					detail: this.detail,
					shopSupplierId: this.shopSupplierId,
					productSpecArr: this.specData != null ? new Array(this.specData.specAttr.length) : [],
					showSku: {
						skuImage: '',
						seckillPrice: 0,
						attr: [],
						productSkuId: [],
						feed: [],
						linePrice: 0,
						seckillStock: 0,
						seckillProductSkuId: 0,
						sum: 1
					}
				};
				this.productModel = obj;
				this.goodDetailModalVisible = true;
			},
			//关闭商品详情模态框
			closeGoodDetailModal(num, res) {
				this.goodDetailModalVisible = false;
				this.clock = false;
				if (num) {
					this.$set(this.good, 'cartNum', this.good.cartNum ? this.good.cartNum + num : num);
					this.reCart(res);
				}
				this.category = {};
				this.good = {};
			},
			//打开/关闭购物车列表popup
			openCartPopup() {
				this.getCart();
				this.cartPopupVisible = !this.cartPopupVisible;
			},
			time_to_sec(time) {
				if (time !== null) {
					var s = '';
					var hour = time.split(':')[0];
					var min = time.split(':')[1];
					s = Number(hour * 3600) + Number(min * 60);
					return s;
				}
			}
		}
	};
</script>

<style lang="scss">
	@import './menu.scss';

	.top-status {
		padding: 0 26rpx;
		height: 104rpx;
		line-height: 104rpx;
		font-size: 28rpx;
		border-bottom: 1rpx solid;
		@include border_color('border_color');
		@include background_color('opacify_background_0');
	}

	.pop-order {
		position: fixed;
		right: 0rpx;
		bottom: 70vh;
		z-index: 8;
		width: 140rpx;
		height: 60rpx;
		word-break: break-all;
		border-top-left-radius: 30rpx;
		border-bottom-left-radius: 30rpx;
		font-size: 26rpx;
		line-height: 60rpx;
		text-align: center;
		@include font_color('text_color');
		@include background_color('background_color');
	}
</style>