<template>
  <view class="login-container" :style="'height: ' + phoneHeight + 'px;'">
    <view class="skip" @click="gotoPage('/pages/index/index')">跳过→</view>
    <view class="p-30-75" v-if="isLogin == 2">
      <view class="login_topbpx">
        <view class="login_tit">注册</view>
        <view class="login_top"
          >已有账户，<text class="red" @click="isLogin = 1"
            >立即登录</text
          ></view
        >
      </view>
      <view class="group-bd">
        <view class="form-level d-s-c">
          <view class="val flex-1 input_botom"
            ><input
              type="text"
              v-model="register.mobile"
              placeholder="请填写手机号"
              :disabled="is_send"
          /></view>
        </view>
        <view class="form-level d-s-c">
          <view class="val flex-1 input_botom"
            ><input
              type="text"
              password="true"
              v-model="register.password"
              placeholder="请输入密码"
          /></view>
        </view>
        <view class="form-level d-s-c">
          <view class="val flex-1 input_botom"
            ><input
              type="text"
              password="true"
              v-model="register.repassword"
              placeholder="请确认密码"
          /></view>
        </view>
        <view class="form-level d-s-c" v-if="smsOpen">
          <view class="val flex-1 d-b-c input_botom">
            <input
              class="flex-1"
              type="number"
              v-model="register.code"
              placeholder="请填写验证码"
            />
            <button
              class="get-code-btn"
              type="default"
              @click="sendCode"
              :disabled="is_send"
            >
              {{ sendBtnTxt }}
            </button>
          </view>
        </view>
      </view>
    </view>
    <view class="p-30-75" v-if="isLogin == 1">
      <view class="login_topbpx">
        <view class="login_tit">登录</view>
        <view class="login_top"
          >还没有账号，<text class="red" @click="isLogin = 2"
            >立即注册</text
          ></view
        >
      </view>
      <view class="group-bd">
        <view class="form-level d-s-c">
          <view class="val flex-1 input_botom"
            ><input
              type="text"
              v-model="formData.mobile"
              placeholder="请填写手机号"
          /></view>
        </view>
        <view class="form-level d-s-c">
          <view class="val flex-1 input_botom"
            ><input
              type="text"
              password="true"
              v-model="logingPassword"
              v-if="!is_code"
              placeholder="请输入密码"
          /></view>
        </view>
        <view class="form-level d-s-c" v-if="is_code && smsOpen">
          <view class="val flex-1 d-b-c input_botom">
            <input
              class="flex-1"
              type="number"
              v-model="formData.code"
              placeholder="请填写验证码"
            />
            <button
              class="get-code-btn"
              type="default"
              @click="sendCode"
              :disabled="is_send"
            >
              {{ sendBtnTxt }}
            </button>
          </view>
        </view>
      </view>
    </view>
    <view class="p-30-75" v-if="isLogin == 0">
      <view class="login_topbpx">
        <view class="login_tit">重置密码</view>
        <view class="login_top"
          ><text class="red" @click="isLogin = 1">立即登录</text></view
        >
      </view>
      <view class="group-bd">
        <view class="form-level d-s-c">
          <view class="val flex-1 input_botom"
            ><input
              type="text"
              v-model="resetpassword.mobile"
              placeholder="请填写手机号"
              :disabled="is_send"
          /></view>
        </view>
        <view class="form-level d-s-c">
          <view class="val flex-1 input_botom"
            ><input
              type="text"
              password="true"
              v-model="resetpassword.password"
              placeholder="请输入密码"
          /></view>
        </view>
        <view class="form-level d-s-c">
          <view class="val flex-1 input_botom"
            ><input
              type="text"
              password="true"
              v-model="resetpassword.repassword"
              placeholder="请确认密码"
          /></view>
        </view>
      </view>
    </view>
    <view
      v-if="isLogin == 1"
      class="gray6 p-0-75"
      :class="is_code ? 'd-e-c' : 'd-b-c'"
    >
      <view v-if="!is_code" @click="isLogin = 0">忘记密码?</view>
      <view @click="isCode()" v-if="smsOpen">{{
        is_code ? "使用密码登录" : "使用验证码登录"
      }}</view>
    </view>
    <view style="padding-top: 80rpx" class="btns p-30-75" v-if="isLogin == 2"
      ><button type="default" @click="registerSub">注册</button></view
    >
    <view style="padding-top: 80rpx" class="btns p-30-75" v-if="isLogin == 1"
      ><button type="default" @click="formSubmit">登录</button></view
    >
    <view style="padding-top: 80rpx" class="btns p-30-75" v-if="isLogin == 0"
      ><button type="default" @click="resetpasswordSub">重置密码</button></view
    >
  </view>
</template>

<script>
import { gotopage } from "@/common/gotopage.js";
export default {
  data() {
    return {
      /*表单数据对象*/
      formData: {
        /*手机号*/
        mobile: "",
        /*验证码*/
        code: "",
      },
      logingPassword: "",
      register: {
        mobile: "",
        password: "",
        repassword: "",
        code: "",
      },
      resetpassword: {
        mobile: "",
        password: "",
        repassword: "",
        code: "",
      },
      /*是否已发验证码*/
      is_send: false,
      /*发送按钮文本*/
      sendBtnTxt: "获取验证码",
      /*当前秒数*/
      second: 60,
      ip: "",
      isShow: true,
      isLogin: 1,
      is_code: false,
      phoneHeight: 0,
      smsOpen: false,
    };
  },
  onLoad() {},
  onShow() {
    this.getCodeType();
    this.init();
  },
  methods: {
    getCodeType() {
      let self = this;
      self._get("index/loginSetting", {}, (res) => {
        self.smsOpen = res.data.smsOpen;
        self.is_code = self.smsOpen;
      });
    },
    /*初始化*/
    init() {
      let self = this;
      uni.getSystemInfo({
        success(res) {
          self.phoneHeight = res.windowHeight;
        },
      });
    },
    isCode() {
      if (this.is_code) {
        this.$set(this, "is_code", false);
      } else {
        this.$set(this, "is_code", true);
      }
    },
    /*提交*/
    formSubmit() {
      let self = this;
      let formdata = {
        mobile: self.formData.mobile,
      };
      let url = "";
      if (!/^1(3|4|5|6|7|8|9)\d{9}$/.test(self.formData.mobile)) {
        uni.showToast({
          title: "手机有误,请重填！",
          duration: 2000,
          icon: "none",
        });
        return;
      }
      if (self.is_code) {
        if (self.formData.code == "") {
          uni.showToast({
            title: "验证码不能为空！",
            duration: 2000,
            icon: "none",
          });
          return;
        }
        formdata.code = self.formData.code;
        url = "user/userOpen/smslogin";
      } else {
        if (self.logingPassword == "") {
          uni.showToast({
            title: "密码不能为空！",
            duration: 2000,
            icon: "none",
          });
          return;
        }
        formdata.password = self.logingPassword;
        url = "user/userOpen/phoneLogin";
      }

      uni.showLoading({
        title: "正在提交",
      });
      self._post(
        url,
        formdata,
        (result) => {
          // 记录token userId
          uni.setStorageSync("token", result.data.token);
          uni.setStorageSync("user_id", result.data.loginUserVo.userId);
          // 获取登录前页面
          let url = "/" + uni.getStorageSync("currentPage");
          let pageOptions = uni.getStorageSync("currentPageOptions");
          if (Object.keys(pageOptions).length > 0) {
            url += "?";
            for (let i in pageOptions) {
              url += i + "=" + pageOptions[i] + "&";
            }
            url = url.substring(0, url.length - 1);
          }
          console.log(url,'url');
          // 执行回调函数
          self.gotoPage(url, "redirect");
          // uni.navigateBack({
          //   delta: 2,
          // });
        },
        false,
        () => {
          uni.hideLoading();
        }
      );
    },
    /*注册*/
    registerSub() {
      let self = this;
      if (!/^1(3|4|5|6|7|8|9)\d{9}$/.test(self.register.mobile)) {
        uni.showToast({
          title: "手机有误,请重填！",
          duration: 2000,
          icon: "none",
        });
        return;
      }
      if (self.register.code == "" && self.smsOpen) {
        uni.showToast({
          title: "验证码不能为空！",
          duration: 2000,
          icon: "none",
        });
        return;
      }
      if (self.register.password.length < 6) {
        uni.showToast({
          title: "密码至少6位数！",
          duration: 2000,
          icon: "none",
        });
        return;
      }
      if (self.register.password !== self.register.repassword) {
        uni.showToast({
          title: "两次密码输入不一致！",
          duration: 2000,
          icon: "none",
        });
        return;
      }
      self.register.invitationId = uni.getStorageSync("invitationId") || 0;
      self.register.refereeId = uni.getStorageSync("refereeId") || 0;
      uni.showLoading({
        title: "正在提交",
      });
      self._postBody(
        "user/userOpen/register",
        self.register,
        (result) => {
          uni.showToast({
            title: "注册成功",
            duration: 3000,
          });
          self.formData.mobile = self.register.mobile;
          self.register = {
            mobile: "",
            password: "",
            repassword: "",
          };
          self.second = 0;
          self.changeMsg();
          self.isLogin = 1;
        },
        false,
        () => {
          uni.hideLoading();
        }
      );
    },
    /*改变发送验证码按钮文本*/
    changeMsg() {
      if (this.second > 0) {
        this.sendBtnTxt = this.second + "秒";
        this.second--;
        setTimeout(this.changeMsg, 1000);
      } else {
        this.sendBtnTxt = "获取验证码";
        this.second = 60;
        this.is_send = false;
      }
    },
    /*发送短信*/
    sendCode() {
      let self = this;
      if (self.isLogin == 1) {
        if (!/^1(3|4|5|6|7|8|9)\d{9}$/.test(self.formData.mobile)) {
          uni.showToast({
            title: "手机有误,请重填！",
            duration: 2000,
            icon: "none",
          });
          return;
        }
      } else if (self.isLogin == 2) {
        if (!/^1(3|4|5|6|7|8|9)\d{9}$/.test(self.register.mobile)) {
          uni.showToast({
            title: "手机有误,请重填！",
            duration: 2000,
            icon: "none",
          });
          return;
        }
      } else if (self.isLogin == 0) {
        if (!/^1(3|4|5|6|7|8|9)\d{9}$/.test(self.resetpassword.mobile)) {
          uni.showToast({
            title: "手机有误,请重填！",
            duration: 2000,
            icon: "none",
          });
          return;
        }
      }

      let type = "register";
      let mobile = self.register.mobile;
      if (self.isLogin == 1) {
        type = "login";
        mobile = self.formData.mobile;
      } else if (self.isLogin == 0) {
        type = "login";
        mobile = self.resetpassword.mobile;
      }
      self._post(
        "user/userOpen/sendCode",
        {
          mobile: mobile,
          type: type,
        },
        (result) => {
          if (result.code == 1) {
            uni.showToast({
              title: "发送成功",
            });
            self.is_send = true;
            self.changeMsg();
          }
        }
      );
    },
    resetpasswordSub() {
      let self = this;
      if (!/^1(3|4|5|6|7|8|9)\d{9}$/.test(self.resetpassword.mobile)) {
        uni.showToast({
          title: "手机有误,请重填！",
          duration: 2000,
          icon: "none",
        });
        return;
      }
      if (self.resetpassword.password.length < 6) {
        uni.showToast({
          title: "密码至少6位数！",
          duration: 2000,
          icon: "none",
        });
        return;
      }
      if (self.resetpassword.password !== self.resetpassword.repassword) {
        uni.showToast({
          title: "两次密码输入不一致！",
          duration: 2000,
          icon: "none",
        });
        return;
      }

      uni.showLoading({
        title: "正在提交",
      });
      self._post(
        "user/useropen/resetpassword",
        self.resetpassword,
        (result) => {
          uni.showToast({
            title: "重置成功",
            duration: 3000,
          });
          self.formData.mobile = self.resetpassword.mobile;
          self.resetpassword = {
            mobile: "",
            password: "",
            repassword: "",
          };
          self.second = 0;
          self.changeMsg();
          self.isLogin = 1;
        },
        false,
        () => {
          uni.hideLoading();
        }
      );
    },
  },
};
</script>

<style lang="scss" scoped>
page {
  background-color: #ffffff;
}

.p-30-75 {
  padding: 30rpx 75rpx;
}

.p-0-75 {
  padding: 0 75rpx;
}

.t-r {
  text-align: right;
}

.login-container {
  position: relative;
  background: #ffffff;
}

.login-container input {
  height: 88rpx;
  line-height: 88rpx;
}

.wechatapp {
  padding: 80rpx 0 48rpx;
  border-bottom: 1rpx solid #e3e3e3;
  margin-bottom: 72rpx;
  text-align: center;
}

.wechatapp .header {
  width: 190rpx;
  height: 190rpx;
  border: 2px solid #fff;
  margin: 0rpx auto 0;
  border-radius: 50%;
  overflow: hidden;
  box-shadow: 1px 0px 5px rgba(50, 50, 50, 0.3);
}

.auth-title {
  color: #585858;
  font-size: 34rpx;
  margin-bottom: 40rpx;
}

.auth-subtitle {
  color: #888;
  margin-bottom: 88rpx;
  font-size: 28rpx;
}

.login-btn {
  padding: 0 20rpx;
}

.login-btn button {
  height: 88rpx;
  line-height: 88rpx;
  background: #04be01;
  color: #fff;
  font-size: 30rpx;
  border-radius: 999rpx;
  text-align: center;
}

.no-login-btn {
  margin-top: 20rpx;
  padding: 0 20rpx;
}

.no-login-btn button {
  height: 88rpx;
  line-height: 88rpx;
  background: #dfdfdf;
  color: #fff;
  font-size: 30rpx;
  border-radius: 999rpx;
  text-align: center;
}

.get-code-btn {
  width: 200rpx;
  height: 80rpx;
  line-height: 76rpx;
  padding: 0rpx 30rpx;
  border-radius: 40rpx;
  white-space: nowrap;
  // border: 1rpx solid $dominant-color;
  background-color: #ffffff;
  color: $dominant-color;
  font-size: 30rpx;
}

.get-code-btn[disabled="true"] {
  // border: 1rpx solid #cccccc;
  background-color: #ffffff;
}
.get-code-btn::after {
  display: none;
}
.btns button {
  height: 90rpx;
  line-height: 90rpx;
  font-size: 34rpx;
  border-radius: 45rpx;
  background: $dominant-color;
  color: #ffffff;
}

.login_topbpx {
  padding: 181rpx 0;
  padding-bottom: 110rpx;
}

.login_tit {
  font-size: 52rpx;
  font-weight: 600;
  margin-bottom: 33rpx;
}

.login_top {
  font-size: 24rpx;
  color: #adafb3;
}

.input_botom {
  border-bottom: 1px solid #f4f4f4;
}

.bottom_nav {
  width: 100%;
  position: absolute;
  bottom: 100rpx;
}

.bottom-box {
  width: 70%;
  margin: 0 auto;
}

.other_tit {
  height: 1rpx;
  background-color: #cacaca;
  width: 100%;
  line-height: 1rpx;
  text-align: center;
}

.weixin_box {
  background-color: #04be01;
  border-radius: 50%;
  width: 80rpx;
  height: 80rpx;
  line-height: 80rpx;
  text-align: center;
}

.weixin_box .icon-weixin {
  font-size: 40rpx;
  color: #ffffff;
}

// .btns .bg-green{
// 	background-color: #04BE01;
// }
.skip {
  position: absolute;
  top: 80rpx;
  right: 30rpx;
  font-size: 28rpx;
  color: #999999;
}
</style>
