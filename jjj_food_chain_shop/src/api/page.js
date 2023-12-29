import request from '@/utils/request'

let PageApi = {

    /*获取数据*/
    detail(data, errorback) {
        return request._post('/shop/page/page/detail', data, errorback);
    },
    /*上架样式*/
    toAddPage(data, errorback) {
        return request._get('/shop/page/page/add', data, errorback);
    },
    /*上架样式*/
    addPage(data, errorback) {
        return request._post('/shop/page/page/add', data, errorback);
    },
    /*首页模板列表*/
    HomeList(data, errorback) {
        return request._postBody('/shop/page/page/list', data, errorback);
    },
    /*页面列表*/
    PageList(data, errorback) {
        return request._postBody('/shop/page/page/index', data, errorback);
    },
    /*删除页面*/
    deletePage(data, errorback) {
        return request._post('/shop/page/page/delete', data, errorback);
    },
    /*设为首页*/
    setHome(data, errorback) {
        return request._post('/shop/page/page/setHome', data, errorback);
    },
    /*设为首页*/
    setPage(data, errorback) {
        return request._post('/shop/page/page/setPage', data, errorback);
    },
    /*编辑页面*/
    editPage(data, errorback) {
        return request._get('/shop/page/page/edit', data, errorback);
    },
    /*编辑页面*/
    pageEdit(data, errorback) {
        return request._post('/shop/page/page/edit', data, errorback);
    },
    /*保存编辑页面*/
    addhome(data, errorback) {
        return request._post('/shop/page/page/addPage', data, errorback);
    },
    /*编辑页面*/
    getHome(data, errorback) {
        return request._get('/shop/page/page/addPage', data, errorback);
    },
    getEdit(data, errorback) {
        return request._get('/shop/page/page/editPage', data, errorback);
    },
    editHome(data, errorback) {
        return request._post('/shop/page/page/editPage', data, errorback);
    },
    /*获取分类*/
    getCategory(data, errorback) {
        return request._get('/shop/page/page/category', data, errorback);
    },
    /*提交分类*/
    postCategory(data, errorback) {
        return request._postBody('/shop/page/page/category', data, errorback);
    },
    getTabbar(data, errorback) {
        return request._get('/shop/page/tabbar/index', data, errorback);
    },
    editTabbar(data, errorback) {
        return request._postBody('/shop/page/tabbar/edit', data, errorback);
    },
    /*广告列表*/
    menuList(data, errorback) {
        return request._postBody('/shop/page/page/myMenu/index', data, errorback);
    },
    /*添加广告*/
    addMenu(data, errorback) {
        return request._postBody('/shop/page/page/myMenu/add', data, errorback);
    },
    /*广告详情*/
    menuDetail(data, errorback) {
        return request._get('/shop/page/page/myMenu/detail', data, errorback);
    },
    /*修改广告*/
    editMenu(data, errorback) {
        return request._postBody('/shop/page/page/myMenu/edit', data, errorback);
    },
    /*删除广告*/
    deleteMenu(data, errorback) {
        return request._post('/shop/page/page/myMenu/delete', data, errorback);
    },
    /*主题详情*/
    themeDetail(data, errorback) {
        return request._get('/shop/page/page/theme/index', data, errorback);
    },
    /*修改广告*/
    editTheme(data, errorback) {
        return request._postBody('/shop/page/page/theme/index', data, errorback);
    },
}

export default PageApi;
