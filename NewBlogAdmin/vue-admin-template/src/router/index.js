import Vue from 'vue'
import Router from 'vue-router'
import store from '@/store'

Vue.use(Router)

/* Layout */
import Layout from '@/layout'

/**
 * Note: sub-menu only appear when route children.length >= 1
 * Detail see: https://panjiachen.github.io/vue-element-admin-site/guide/essentials/router-and-nav.html
 *
 * hidden: true                   if set true, item will not show in the sidebar(default is false)
 * alwaysShow: true               if set true, will always show the root menu
 *                                if not set alwaysShow, when item has more than one children route,
 *                                it will becomes nested mode, otherwise not show the root menu
 * redirect: noRedirect           if set noRedirect will no redirect in the breadcrumb
 * name:'router-name'             the name is used by <keep-alive> (must set!!!)
 * meta : {
    roles: ['admin','editor']    control the page roles (you can set multiple roles)
    title: 'title'               the name show in sidebar and breadcrumb (recommend set)
    icon: 'svg-name'/'el-icon-x' the icon show in the sidebar
    breadcrumb: false            if set false, the item will hidden in breadcrumb(default is true)
    activeMenu: '/example/list'  if set path, the sidebar will highlight the path you set
  }
 */

/**
 * constantRoutes
 * a base page that does not have permission requirements
 * all roles can be accessed
 */
export const constantRoutes = [
    {
        path: '/login',
        component: () => import('@/views/login/index'),
        hidden: true
    },


    {
        path: '/404',
        component: () => import('@/views/404'),
        hidden: true
    },

    {
        path: '/gotowechat',
        component: () => import('@/views/gotowechat'),
        hidden: true
    },

    {
        path: '/url_scan',
        component: () => import('@/views/url_scan'),
        hidden: true
    },

    {
        path: '/',
        component: Layout,
        redirect: '/dashboard',
        children: [{
            path: 'dashboard',
            name: 'Dashboard',
            component: () => import('@/views/dashboard/index'),
            meta: { title: '控制面板', icon: 'dashboard' }
        }]
    },

    {
        path: '/data',
        component: Layout,
        redirect: '/user',
        name: 'Data',
        meta: { title: '数据管理', icon: 'form' },
        children: [
            {
                path: 'product',
                name: 'Tree',
                component: () => import('@/views/data/productdata'),
                meta: { title: '文章管理', icon: 'tree' }
            },
            {
                path: 'front',
                name: 'front',
                component: () => import('@/views/data/homemanage'),
                meta: { title: '首页管理', icon: 'example' }
            },
            {
                path: 'fenlei',
                name: 'fenlei',
                component: () => import('@/views/data/fenleimanage'),
                meta: { title: '分类管理', icon: 'link' }
            },
            {
                path: 'biaoqian',
                name: 'biaoqian',
                component: () => import('@/views/data/biaoqianmanage'),
                meta: { title: '标签管理', icon: 'form' }
            },
            {
                path: 'photomanage',
                name: 'photomanage',
                component: () => import('@/views/data/photomanage'),
                meta: { title: '图片管理', icon: 'nested' }
            },
            /*
      {
        path: 'productRelated',
        name: 'Tree',
        component: () => import('@/views/data/productRelatedData'),
        meta: { title: '商品关联管理', icon: 'form' }
      }
      */
        ]
    },


    {
        path: '/all',
        component: Layout,
        name: 'all',
        meta: { title: '杂项管理', icon: 'table' },
        children: [
            {
                path: 'comment',
                name: 'comment',
                component: () => import('@/views/data/Tmanage'),
                meta: { title: '评论管理', icon: 'message' }
            },
            {
                path: 'friend',
                name: 'friend',
                component: () => import('@/views/data/Tmanage'),
                meta: { title: '友链管理', icon: 'peoples' }
            },
            {
                path: 'chat',
                name: 'chat',
                component: () => import('@/views/data/Tmanage'),
                meta: { title: '聊天管理', icon: 'message' }
            },
            {
                path: 'fan',
                name: 'fan',
                component: () => import('@/views/data/Tmanage'),
                meta: { title: '关注管理', icon: 'link' }
            },
            {
                path: 'kefu_chat_history',
                name: 'kefu_chat_history',
                component: () => import('@/views/data/Tmanage'),
                meta: { title: '客服管理', icon: 'message' }
            },
            {
                path: 'user',
                name: 'user',
                component: () => import('@/views/data/Tmanage'),
                meta: { title: '用户管理', icon: 'user' }
            },
            {
                path: 'user_agent_details',
                name: 'user_agent_details',
                component: () => import('@/views/data/Tmanage'),
                meta: { title: '访客管理', icon: 'eye-open' }
            },
            {
                path: 'user_option',
                name: 'user_option',
                component: () => import('@/views/data/Tmanage'),
                meta: { title: '设置管理', icon: 'password' }
            },
            {
                path: 'action',
                name: 'action',
                component: () => import('@/views/data/Tmanage'),
                meta: { title: '通知管理', icon: 'password' }
            },
   
        ]
    },

    {
        path: '/user',
        component: Layout,
        name: 'Data',
        meta: { title: '旧版管理', icon: 'peoples' },
        children: [
            {
                path: 'allinfo',
                name: 'Table',
                component: () => import('@/views/data/userdata'),
                meta: { title: '用户管理', icon: 'peoples' }
            },
            {
                path: 'visitor',
                name: 'Form',
                component: () => import('@/views/history/visitor'),
                meta: { title: '访客记录', icon: 'peoples' }
            },
            {
                path: 'selfinfo',
                name: 'info',
                component: () => import('@/views/otheruserinfo/VE'),
                meta: { title: '个人信息', icon: 'user' }
            },
   
        ]
    },

    {
        path: '/user_agent_details',
        name: 'user_agent_details',
        component: Layout,
        children: [{
            path: '/user_agent_details',
            name: 'user_agent_details',
            component: () => import('@/views/data/Tmanage'),
            meta: { title: '历史访客', icon: 'eye-open' }
        },]
    },
    /*
    {
        path: '/user',
        component: Layout,
        name: 'user',
        meta: { title: '信息', icon: 'user' },
        children: [
            {
                path: 'info',
                name: 'info',
                component: () => import('@/views/otheruserinfo/VE'),
                meta: { title: '个人信息', icon: 'user' }
            },
   
        ]
    },
    */





    // 404 page must be placed at the end !!!
    { path: '*', redirect: '/404', hidden: true }
]

const createRouter = () => new Router({
    mode: 'history', // require service support
    scrollBehavior: () => ({ y: 0 }),
    routes: constantRoutes,
    base: '/xiba-newblog-admin'
})

const router = createRouter()

// Detail see: https://github.com/vuejs/vue-router/issues/1234#issuecomment-357941465
export function resetRouter() {
    const newRouter = createRouter()
    router.matcher = newRouter.matcher // reset router
}

// 在完成路由跳转后执行一些逻辑
router.afterEach((to, from) => {

    // 示例：输出一条日志
    console.log(`Navigated from ${from.path} to ${to.path}`);
    if(to.fullPath!=='/login')sessionStorage.setItem('redirectPath', to.fullPath);

    // 在这里可以执行其他需要在路由跳转后进行的操作
});

// 在完成路由跳转后执行一些逻辑
router.beforeEach((to, from, next) => {
    // 示例：输出一条日志
    console.log(`Ready to navigate from ${from.path} to ${to.path}`);

    // login
    if(!store.state.app.UserId && to.path!=='/login'){
        console.log("状态未登录:"+store.state.app.UserId)
        router.push('/login')
    }

    next();
});


export default router
