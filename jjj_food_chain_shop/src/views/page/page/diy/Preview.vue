<template>
  <div class="diy-phone-container">
    <!--顶部设置栏-->
    <!--顶部设置栏-->
    <div class="diy-phone-item" :class="{ active: form.selectedIndex < 0 }">
      <!-- <Setpages  v-if="diyType != 'center'" :diyData="diyData"></Setpages> -->
      <Setcenter v-if="diyType == 'center'" :diyData="diyData"></Setcenter>
    </div>
    <div class="wrapper">
      <div
        class="diy-phone-item"
        v-for="(item, index) in diyData.items"
        :key="item.id"
      >
        <!-- 图片轮播 -->
        <template v-if="item.type == 'banner'">
          <Banner
            :item="item"
            :index="index"
            :selectedIndex="form.selectedIndex"
          ></Banner>
        </template>
        <!-- 橱窗-->
        <template v-else-if="item.type == 'window'">
          <Window
            :item="item"
            :index="index"
            :selectedIndex="form.selectedIndex"
          ></Window>
        </template>
        <template v-else-if="item.type == 'adNav'">
          <adNav
            :item="item"
            :index="index"
            :selectedIndex="form.selectedIndex"
          ></adNav>
        </template>
        <!--导航组-->
        <template v-else-if="item.type == 'navBar'">
          <NavBar
            :item="item"
            :index="index"
            :selectedIndex="form.selectedIndex"
          ></NavBar>
        </template>
        <!--辅助空白-->
        <template v-else-if="item.type == 'blank'">
          <Blank
            :item="item"
            :index="index"
            :selectedIndex="form.selectedIndex"
          ></Blank>
        </template>
        <!--辅助线-->
        <template v-else-if="item.type == 'guide'">
          <Guide
            :item="item"
            :index="index"
            :selectedIndex="form.selectedIndex"
          ></Guide>
        </template>
      </div>
    </div>
  </div>
</template>

<script>
import Banner from "./model/Banner.vue";
import Window from "./model/Window.vue";
import NavBar from "./model/NavBar.vue";
import Blank from "./model/Blank.vue";
import Guide from "./model/Guide.vue";
import adNav from "./model/adNav.vue";
export default {
  components: {
    /*图片轮播组件*/
    Banner,
    adNav,
    /*图片橱窗*/
    Window,
    /*导航组*/
    NavBar,
    /*辅助空白*/
    Blank,
    /*辅助线*/
    Guide,
  },
  data() {
    return {};
  },
  props: {
    form: Object,
    defaultData: Object,
    diyData: Object,
    diyType: String,
  },
  created() {
    console.log(this.diyData);
  },
  methods: {
    /*删除diy元素*/
    onDeleleItem: function (index) {
      let self = this;
      ElMessageBox.confirm("确定要删除吗?", "提示", {
        type: "warning",
      }).then(() => {
        self.diyData.items.splice(index, 1);
        self.form.selectedIndex = -1;
      });
    },

    /*编辑当前选中的Diy元素*/
    onEditer: function (index) {
      let self = this;
      // 记录当前选中元素的索引
      self.form.selectedIndex = index;
      // 当前选中的元素数据
      self.form.curItem =
        self.form.selectedIndex < 0
          ? self.diyData.page
          : self.diyData.items[self.form.selectedIndex];
      // 注册编辑器事件
      //self.initEditor();
    },

    /* 注册编辑器事件*/
    initEditor: function () {
      let self = this;
      // 注册dom事件
      self.$nextTick(function () {
        // 销毁 umeditor 组件
        if (self.form.umeditor.hasOwnProperty("key")) {
          self.form.umeditor.destroy();
        }
        // 注册html组件
        self.editorHtmlComponent();
        // 富文本事件
        if (self.form.curItem.type === "richText") {
          //self.onRichText(self.form.curItem);
        }
      });
    },

    /*编辑器事件：html组件*/
    editorHtmlComponent: function () {
      let self = this;
      var editor = self.$refs["diy-editor"];
      // 单/多选框
      //editor.find('input[type=checkbox], input[type=radio]').uCheck();
      // select组件
      // $editor.find('select').selected();
    },
  },
};
</script>

<style lang="scss" scoped>
.diy-phone-container {
  height: 100%;
  position: relative;
  // height: calc(100vh - 150px);
}
.diy-phone-container .wrapper {
  height: calc(100% - 90px);
  overflow-y: auto;
}
.diy-phone-container .phone-top {
  padding: 0 20px;
  border-radius: 18px 18px 0 0;
}
.diy-phone-container .phone-top .status-bar {
  height: 20px;
  display: flex;
  justify-content: space-between;
}
.diy-phone-container .phone-top .svg-icon {
  width: 20px;
  height: 20px;
  color: #333333;
}
.diy-phone-container .phone-top .navigation {
  height: 44px;
  line-height: 44px;
  text-align: center;
  font-size: 18px;
}
.diy-phone-container .diy-phone-item {
}
.diy-phone-container .diy-phone-item > div {
  position: relative;
  border: 2px solid #f1f1f2;
  border: none;
}
.diy-phone-container .diy-phone-item > div:hover,
.diy-phone-container .diy-phone-item.active > div {
  border: 2px dashed #3a8ee6;
}
.diy-phone-container img {
  width: 100%;
}
</style>
