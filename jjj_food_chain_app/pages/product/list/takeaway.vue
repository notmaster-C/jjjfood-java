<template>
	<view class="container" :data-theme="theme()" :class="theme() || ''">
		<view class="main">
			<view class="nav">
				<view class="header">
					<view class="left" @click="selectShop('/pages/shop/shop_storelist')">
						<view class="store-name">
							<text class="fb">{{ supplier.name }}</text>
							<view class="iconfont icon icon-jiantou"></view>
						</view>
						<view class="gray9 f22">距离：{{ supplier.distance }}</view>
					</view>
					<view class="right">
						<template v-for="(item, index) in delivery_set" :key="item">
							<view class="takeout" v-if="item == '10'" :class="orderType == 'takeout' ? 'active' : ''"
								@tap="takout">
								<text>配送</text>
							</view>
						</template>
						<template v-for="(item, index) in delivery_set" :key="item">
							<view class="dinein" v-if="item == '20'" :class="orderType == 'takein' ? 'active' : ''"
								@tap="takein">
								<text>自取</text>
							</view>
						</template>
					</view>
				</view>
				<view class="reduce_list">
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
							<view v-if="item.products.length != 0" class="menu d-s-c" :id="`menu-${item.categoryId}`"
								:class="{ current: item.categoryId === currentCateId }"
								@tap="handleMenuTap(item.categoryId)">
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
												<view class="ww100">
													<view class="name">{{ good.productName }}</view>
													<view class="tips text-ellipsis">{{ good.sellingPoint }}</view>
												</view>
												<view class="price_and_action">
													<view>
														<text style="color: #FF5800;font-size: 22rpx;">￥</text>
														<text class="price">{{ good.productPrice }}</text>
														<text class="linprice"
															v-if="good.productPrice * 1 != good.linePrice * 1">￥{{ good.linePrice * 1 }}</text>
													</view>
													<view class="btn-group" v-if="good.specTypes == 20">
														<button type="primary" class="btn property_btn"
															hover-class="none" size="min"
															@click.stop="gotoDetail(good)">选规格</button>
														<view class="dot" v-if="good.cartNum != 0">{{ good.cartNum }}
														</view>
													</view>
													<view class="btn-group" v-else>
														<button type="default" v-if="good.cartNum != 0" plain
															class="btn reduce_btn" size="min" hover-class="none"
															@tap.stop="reduceFunc(good)">
															<view class="icon icon-jian iconfont iconsami-select">
															</view>
														</button>
														<view class="number" v-if="good.cartNum != 0">
															{{ good.cartNum }}
														</view>
														<button type="primary" class="btn add_btn" size="min"
															hover-class="none" @tap.stop="addCart(good)">
															<view class="icon icon-jia iconfont iconadd-select"></view>
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
						<text class="gray9 f22 text-d-line fn ml10">￥{{ linePrice }}</text>
					</view>
					<view class="gray9 f22 fn" v-if="totalBagPrice != 0">
						<text class="">包含包装费￥</text>
						{{ totalBagPrice }}
					</view>
				</view>
				<button class="pay-btn" @tap="toPay" v-if="minMoneyDiff <= 0 || orderType != 'takeout'">去结算</button>
				<button class="btn-gray mr10" disabled
					v-if="minMoneyDiff > 0 && totalPrice == 0 && orderType == 'takeout'">{{ '￥' + minMoney + '起送' }}</button>
				<button class="btn-gray mr10" disabled
					v-if="minMoneyDiff > 0 && totalPrice != 0 && orderType == 'takeout'">{{ '还差￥' + minMoneyDiff + '起送' }}</button>
			</view>
			<!-- 购物车栏 end -->
		</view>
		<!-- 购物车详情popup -->
		<popup-layer direction="top" :show-pop="cartPopupVisible" class="cart-popup" v-if="cartTotalNum > 0">
			<template #content>
				<view class="cart-popup pr">
					<view class="d-b-c top-title" v-if="reduce&&(reduce.now|| reduce.next)">
						<view class="mj d-c-c">
							<view v-if="reduce.now">
								已
								<text class="">{{ reduce.now.activeName }}</text>
								，
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
												<text class="f24 gray9"
													v-if="bag_type != 1">(包装费：￥{{ item.bagPrice }})</text>
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
			popupLayer
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
				cart_list: [],
				orderType: '',
				takeout_address: {},
				phoneHeight: 0,
				/*可滚动视图区域高度*/
				scrollviewHigh: 0,
				pickTimeList:['00:00', '00:00'],
				deliveryTimeList: ['00:00', '00:00'],
				store_time: ['00:00', '00:00'],
				officeTime: {
					now: 0,
					delivery_start: 0,
					delivery_end: 0,
					store_start: 0,
					store_end: 0
				},
				businessOpen: 1,
				addclock: false,
				longitude: 0,
				latitude: 0,
				bag_type: 1,
				shopSupplierId: 0,
				/* 10配送20自提30店内40外卖 */
				dinnerType: 20,
				cartType: 0,
				delivery_set: [],
				isFirst: true,
				addressId: 0,
				minMoney: 0,
				minMoneyDiff: 0,
				reduceList: [],
				totalBagPrice: 0,
				reduce: {},
				reduceDiffValue: 0,
				linePrice: 0
			};
		},
		onLoad(e) {
			let self = this;
			self.orderType = e.orderType;
			self.shopSupplierId = uni.getStorageSync('selectedId') ? uni.getStorageSync('selectedId') : 0;
			self.$fire.on('takeout', function(e) {
				if (e) {
					self.orderType = 'takeout';
					self.dinnerType = 10;
				}
			});
			self.$fire.on('selectShop', function(e) {
				if (e) {
					self.shopSupplierId = uni.getStorageSync('selectedId') || 0;
				}
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
			scrollInit() {
				let self = this;
				if(self.scrollviewHigh){
					return
				}
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
			init() {
				//页面初始化
				this.addclock = false;
				this.category = {};
				this.good = {};
				this.goodDetailModalVisible = false;
				this.clock = false;
				this.loading = true;
				this.isLoading = true;
				this.goods_list = [];
				
				this.getcityData();
				this.getCategory();
				
				this.sizeCalcState = false;
			},
			/* 获取商品类型 */
			getCategory() {
				let self = this;
				this.sizeCalcState = false;
				
				let delivery = self.orderType == 'takeout' ? 10 : 20;
				self._postBody(
					'product/category/index', {
						type: 0,
						/* 0外卖，1店内 */
						shopSupplierId: self.shopSupplierId,
						longitude: self.longitude,
						latitude: self.latitude,
						delivery: delivery,
						ordertype: 0,
						tableId: 0
					},
					function(res) {
						if (self.getUserId() && res.data.addressId == 0 && self.orderType == 'takeout') {
							self.showError('未选择收货地址，请设置收货地址', function() {
								self.gotoPage('/pages/user/address/storeaddress?shopSupplierId=' + self
									.shopSupplierId);
								return;
							});
						}
						self.reduceList = res.data.reduceList;
						self.minMoney = (res.data.supplier.minMoney * 1).toFixed(2);
						self.goods_list = res.data.list;
						self.supplier = res.data.supplier;
						self.delivery_set = res.data.supplier.deliverySetList;
						if (self.isFirst && self.orderType == '') {
							if (self.delivery_set.indexOf(10) != -1) {
								self.orderType = 'takeout';
							} else {
								self.orderType = 'takein';
							}
							self.isFirst = false;
						}
						self.addressId = res.data.addressId;
						self.shopSupplierId = res.data.supplier.shopSupplierId;
						self.bag_type = res.data.supplier.bagType;
						self.loading = false;
						self.isLoading = false;
						self.$nextTick(function() {
							self.scrollInit();
						});
						self.getCart();
						self.isBusiness();
						uni.hideLoading();
					},
					function(err) {
						self.showError(err.msg, () => {
							self.gotoPage('/pages/index/index');
						});
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
				/*详情内容格式化*/
				// item.content = utils.format_content(item.content);
				self.detail = item;
				self.showGoodDetailModal();
			},
			/*获取定位方式*/
			getcityData() {
				let self = this;
				// 第一次，如果是公众号，则初始化微信sdk配置
				// #ifdef H5
				if (self.isWeixin()) {
					let sign = uni.getStorageSync('sign');
					if (!sign) {
						uni.showLoading({
							title: '加载中'
						});
						self._post(
							'index/index', {
								url: window.location.href
							},
							function(res) {
								uni.setStorageSync('sign', res.data.signPackage);
								sign = res.data.signPackage;
								uni.hideLoading();
								self.getWxLocation(sign);
							}
						);
					} else {
						self.getWxLocation(sign);
					}
				} else {
					self.getLocation();
				}
				// #endif
				// #ifndef H5
				self.getLocation();
				// #endif
			},
			/*授权启用定位权限*/
			onAuthorize() {
				let self = this;
				uni.openSetting({
					success(res) {
						if (res.authSetting['scope.userLocation']) {
							self.isAuthor = true;
							setTimeout(() => {
								// 获取用户坐标
								self.getLocation(res => {});
							}, 1000);
						}
					}
				});
			},
			/*获取用户坐标*/
			getLocation(callback) {
				let self = this;
				uni.getLocation({
					type: 'wgs84',
					success(res) {
						self.longitude = res.longitude;
						self.latitude = res.latitude;
						self.getCategory();
					},
					fail(err) {
						self.longitude = 0;
						self.latitude = 0;
						uni.showToast({
							title: '获取定位失败，请点击右下角按钮打开定位权限',
							duration: 2000,
							icon: 'none'
						});
						self.getCategory();
					}
				});
			},
			/* 公众号获取坐标 */
			getWxLocation(signPackage, callback) {
				let self = this;
				var jweixin = require('jweixin-module');
				jweixin.config(JSON.parse(signPackage));
				jweixin.ready(function(res) {
					jweixin.getLocation({
						type: 'wgs84',
						success: function(res) {
							self.longitude = res.longitude;
							self.latitude = res.latitude;
							self.getCategory();
						},
						fail(err) {
							self.longitude = 0;
							self.latitude = 0;
							self.getCategory();
						}
					});
				});
				jweixin.error(function(res) {
					console.log(res);
				});
			},
			reCart(res) {
				let self = this;
				if(res.data.cartInfo!=null){
				self.cartTotalNum = res.data.cartInfo.cartTotalNum;
				self.linePrice = res.data.cartInfo.totalLineMoney;
				self.totalPrice = res.data.cartInfo.totalPayPrice;
				self.totalBagPrice = res.data.cartInfo.totalBagPrice;
				self.minMoneyDiff = res.data.cartInfo.minMoneyDiff;
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
					describe: '',
					goods:goods,
					price: goods.skuList[0].productPrice,
					bagPrice: goods.skuList[0].bagPrice,
					shopSupplierId: goods.shopSupplierId,
					cartType: 0,
					dinnerType: self.dinnerType,
					productPrice: goods.skuList[0].linePrice,
					delivery: self.orderType == 'takeout' ? 10 : 20
				};
				self.addclock = true;
				self._postBody(
					'order/cart/add',
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
				let num = goods.cartNum;
				self.addclock = true;
				self._postBody(
					'order/cart/productSub', {
						productId: productId,
						type: 'down',
						cartType: 0,
						dinnerType: self.dinnerType,
						shopSupplierId: self.shopSupplierId,
						delivery: self.orderType == 'takeout' ? 10 : 20
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
				self._postBody(
					'order/cart/lists', {
						shopSupplierId: self.shopSupplierId,
						cartType: 0,
						delivery: self.orderType == 'takeout' ? 10 : 20
					},
					function(res) {
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
				self._postBody(
					'order/cart/sub', {
						productId: productId,
						totalNum: totalNum,
						cartId: goods.cartId,
						type: 'up',
						cartType: 0,
						dinnerType: self.dinnerType,
						shopSupplierId: self.shopSupplierId,
						delivery: self.orderType == 'takeout' ? 10 : 20
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
				self._postBody(
					'order/cart/sub', {
						productId: productId,
						totalNum: 1,
						cartId: goods.cartId,
						type: 'down',
						cartType: 0,
						dinnerType: self.dinnerType,
						shopSupplierId: self.shopSupplierId,
						delivery: self.orderType == 'takeout' ? 10 : 20
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
			takout() {
				if (this.orderType == 'takeout') return;
				this.orderType = 'takeout';
				this.dinnerType = 10;
				this.isBusiness();
				this.init();
			},
			takein() {
				if (this.orderType == 'takein') return;
				this.orderType = 'takein';
				this.dinnerType = 20;
				this.isBusiness();
				this.init();
			},
			handleMenuTap(id) {
				//点击菜单项事件
				if (!this.sizeCalcState) {
					this.calcSize();
				}
				this.currentCateId = id;
				this.$nextTick(() => (this.cateScrollTop = this.goods_list.find(item => item.categoryId == id).top));
			},
			handleGoodsScroll({
				detail
			}) {
				//商品列表滚动事件
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
			showGoodDetailModal() {
				this.detail.sku.forEach((item, index) => {
					item.checked = false;
				});
				let obj = {
					specData: this.detail.sku,
					detail: this.detail,
					shopSupplierId: this.shopSupplierId,
					productSpecArr: this.specData != null ? new Array(this.specData.specAttr.length) : [],
					show_sku: {
						sku_image: '',
						seckill_price: 0,
						attr: [],
						productSkuId: [],
						feed: [],
						linePrice: 0,
						seckill_stock: 0,
						seckill_product_sku_id: 0,
						sum: 1
					}
				};
				this.productModel = obj;
				this.goodDetailModalVisible = true;
			},
			closeGoodDetailModal(num, res) {
				//关闭饮品详情模态框
				this.goodDetailModalVisible = false;
				this.clock = false;
				if (num) {
					this.$set(this.good, 'cartNum', this.good.cartNum ? this.good.cartNum + num : num);
					this.reCart(res);
				}
				this.category = {};
				this.good = {};
			},
			openCartPopup() {
				//打开/关闭购物车列表popup
				this.getCart();
				this.cartPopupVisible = !this.cartPopupVisible;
			},
			handleCartClear() {
				//清空购物车
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
			clearCart() {
				let self = this;
				self._postBody(
					'order/cart/delete', {
						shopSupplierId: self.shopSupplierId,
						cartType: 0
					},
					function(res) {
						self.cartPopupVisible = false;
						self.cart_list = [];
						self.init();
						self.Refresh();
					}
				);
			},
						        Refresh(){
        window.location.reload();
      },
			isBusiness() {
				let nowH = new Date().getHours();
				let nowM = new Date().getMinutes();
				let now = this.time_to_sec(nowH + ':' + nowM);
				if (this.orderType == 'takeout') {
					if (this.supplier.deliveryTimeList[0] <= now && this.supplier.deliveryTimeList[1] >= now) {
						this.businessOpen = 0;
					} else {
						this.businessOpen = 1;
					}
				} else if (this.orderType == 'takein') {
					if (this.supplier.pickTimeList[0] <= now && this.supplier.pickTimeList[1] >= now) {
						this.businessOpen = 0;
					} else {
						this.businessOpen = 1;
					}
				}
			},
			time_to_sec(time) {
				if (time !== null) {
					var s = '';
					var hour = time.split(':')[0];
					var min = time.split(':')[1];
					s = Number(hour * 3600) + Number(min * 60);
					return s;
				}
			},
			selectShop(url) {
				let self = this;
				let delivery = self.orderType == 'takeout' ? 10 : 20;
				self.gotoPage(url + '?dinnerType=' + delivery);
			},
			toPay() {
				let self = this;
				if (self.addressId == 0 && self.orderType == 'takeout') {
					uni.showModal({
						title: '提示',
						content: '您还没选择收货地址,请先选择收货地址',
						success() {
							self.gotoPage('/pages/user/address/storeaddress?shopSupplierId=' + self.shopSupplierId);
						}
					});
					return;
				}
				uni.showLoading({
					title: '加载中'
				});
				self._postBody(
					'order/cart/lists', {
						shopSupplierId: self.shopSupplierId,
						cartType: 0,
						delivery: self.orderType == 'takeout' ? 10 : 20
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
						let delivery = self.orderType == 'takeout' ? 10 : 20;
						uni.hideLoading();
						uni.navigateTo({
							url: '/pages/order/confirm-order?orderType=cart&cartIds=' +
								arrIds.join(',') +
								'&delivery=' +
								delivery +
								'&shopSupplierId=' +
								self.shopSupplierId +
								'&cartType=0' +
								'&dinnerType=' +
								delivery
						});
					}
				);
			},
			gotoDetail(e) {
				let delivery = this.orderType == 'takeout' ? 10 : 20;
				uni.navigateTo({
					url: '/pages/product/detail/detail?productId=' +
						e.productId +
						'&delivery=' +
						delivery +
						'&bag_type=' +
						this.bag_type +
						'&dinnerType=' +
						this.dinnerType +
						'&cartType=' +
						this.cartType
				});
			}
		}
	};
</script>

<style lang="scss">
	@import './menu.scss';
</style>