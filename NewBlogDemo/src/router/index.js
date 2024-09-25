import Vue from 'vue'
import VueRouter from 'vue-router'
import store from '../store'


Vue.use(VueRouter)

const routes = [
    {
        path: '/',
        name: 'Index',
        component: () => import(/* webpackChunkName: "about1" */ '../views/Index.vue'),
        meta: { title: 'BLOG -Index' },
        redirect: '/home',
        children:[
            {
                path: '/home',
                name: 'home',
                component: () => import(/* webpackChunkName: "about2" */ '../views/home/VE.vue'),
                meta: { title: 'BLOG -首页' }
            },
            {
                path: '/kefu',
                name: 'kefu',
                component: () => import(/* webpackChunkName: "about3" */ '../views/kefu/VE.vue'),
                meta: { title: 'BLOG -客服' }
            },
            {
                path: '/kefu/history',
                name: 'kefu/history',
                component: () => import(/* webpackChunkName: "about4" */ '../views/kefu/history/VE.vue'),
                meta: { title: 'BLOG -客服聊天记录' }
            },
            {
                path: '/product',
                name: 'product',
                component: () => import(/* webpackChunkName: "about5" */ '../views/product/VE.vue'),
                meta: { title: 'BLOG -文章详情' }
            },
            {
                path: '/backend',
                name: 'backend',
                component: () => import(/* webpackChunkName: "about6" */ '../views/backend/VE.vue'),
                meta: { title: 'BLOG -管理页面' }
            },
            {
                path: '/user',
                name: 'user',
                component: () => import(/* webpackChunkName: "aboutu1" */ '../views/user/Index.vue'),
                meta: { title: 'BLOG -用户' },
                redirect: '/user/info',
                children:[
                    {
                        path: 'info',
                        name: 'userinfo',
                        component: () => import(/* webpackChunkName: "aboutu2" */ '../views/user/info/VE.vue'),
                        meta: { title: 'BLOG -用户首页' },

                    },
                    {
                        path: 'otheruserinfo',
                        name: 'otheruserinfo',
                        component: () => import(/* webpackChunkName: "aboutu3" */ '../views/user/otheruserinfo/VE.vue'),
                        meta: { title: 'BLOG -其他用户首页' },

                    },
                    {
                        path: 'fanorguanzhu/:segment1/:segment2',
                        name: 'fanorguanzhu',
                        component: () => import(/* webpackChunkName: "aboutu4" */ '../views/user/fanorguanzhu/VE.vue'),
                        meta: { title: 'BLOG -列表' },

                    },
                    {
                        path: 'info/details',
                        name: 'userinfodetails',
                        component: () => import(/* webpackChunkName: "aboutu5" */ '../views/user/info/details/VE.vue'),
                        meta: { title: 'BLOG -用户信息详情' }
                    },
                    {
                        path: 'info/option',
                        name: 'option',
                        component: () => import(/* webpackChunkName: "aboutu11" */ '../views/user/info/option/VE.vue'),
                        meta: { title: 'BLOG -用户设置' },

                    },
                    {
                        path: 'info/dongtai',
                        name: 'dongtai',
                        component: () => import(/* webpackChunkName: "aboutu11" */ '../views/user/info/dongtai/VE.vue'),
                        meta: { title: 'BLOG -用户动态' },

                    },
                    {
                        path: 'buylist',
                        name: 'userbuylist',
                        component: () => import(/* webpackChunkName: "aboutu6" */ '../views/user/buylist/VE.vue'),
                        meta: { title: 'BLOG -用户购物车' },

                    },
                    {
                        path: 'buylist_result',
                        name: 'userbuylist_result',
                        component: () => import(/* webpackChunkName: "aboutu7" */ '../views/user/buylist_result/VE.vue'),
                        meta: { title: 'BLOG -用户购物车结算' },
                    },
                    {
                        path: 'historylist',
                        name: 'userhistorylist',
                        component: () => import(/* webpackChunkName: "aboutu8" */ '../views/user/historylist/VE.vue'),
                        meta: { title: 'BLOG -用户历史订单' },
                    },
                    {
                        path: 'chat',
                        name: 'userchat',
                        component: () => import(/* webpackChunkName: "aboutu9" */ '../views/user/chat/VE.vue'),
                        meta: { title: 'BLOG -聊天' },
                    },
                    {
                        path: 'chat/history',
                        name: 'userchathistory',
                        component: () => import(/* webpackChunkName: "aboutu10" */ '../views/user/chat/history/VE.vue'),
                        meta: { title: 'BLOG -聊天历史' },
                    },
                    {
                        path: 'chat/history/zan',
                        name: 'zan',
                        component: () => import(/* webpackChunkName: "aboutu10" */ '../views/user/chat/history/Titem/VE.vue'),
                        meta: { title: 'BLOG -点赞' },
                    },
                    {
                        path: 'chat/history/guanzhu',
                        name: 'guanzhu',
                        component: () => import(/* webpackChunkName: "aboutu10" */ '../views/user/chat/history/Titem/VE.vue'),
                        meta: { title: 'BLOG -关注' },
                    },
                    {
                        path: 'chat/history/comment',
                        name: 'comment',
                        component: () => import(/* webpackChunkName: "aboutu10" */ '../views/user/chat/history/Titem/VE.vue'),
                        meta: { title: 'BLOG -评论' },
                    },

                    
                ]
            },
            {
                path: 'guidang',
                name: 'guidang',
                component: () => import(/* webpackChunkName: "aboutu4" */ '../views/guidang/VE.vue'),
                meta: { title: 'BLOG -归档' },

            },
            {
                path: 'friend',
                name: 'friend',
                component: () => import(/* webpackChunkName: "aboutu4" */ '../views/friend/VE.vue'),
                meta: { title: 'BLOG -友链' },

            },
            {
                path: 'about',
                name: 'about',
                component: () => import(/* webpackChunkName: "aboutu4" */ '../views/about/VE.vue'),
                meta: { title: 'BLOG -关于' },

            },
            {
                path: '/other',
                name: 'other',
                component: () => import(/* webpackChunkName: "about9" */ '../views/other/Index.vue'),
                meta: { title: 'BLOG -其他' },
                children:[
                    {
                        path: 'VETa',
                        name: 'VETa',
                        component: () => import(/* webpackChunkName: "about10" */ '../views/other/VETa.vue'),
                        meta: { title: 'BLOG -VETa' },
                    }
                ]
            },
            {
                path: 'vue2-editor',
                name: 'vue2-editor',
                component: () => import(/* webpackChunkName: "about10" */ '../views/other/vue2-editor.vue'),
                meta: { title: 'BLOG -vue2-editor' },
            },
            {
                path: 'test_video',
                name: 'test_video',
                component: () => import(/* webpackChunkName: "about10" */ '../views/other/test_video.vue'),
                meta: { title: 'BLOG -vue2-editor' },
            },
            {
                path: '/404',
                name: '404',
                component: () => import(/* webpackChunkName: "about11" */ '../views/other/VE404.vue'),
                meta: { title: 'BLOG -404' }
            }
        ]
    },
    {
        path: '/login',
        name: 'login',
        component: () => import(/* webpackChunkName: "about12" */ '../views/LogIn.vue'),
        meta: { title: 'BLOG -用户登录' },

    },
    {
        path: '/login3',
        name: 'logIn',
        component: () => import(/* webpackChunkName: "about12_1" */ '../views/LogIn3.vue'),
        meta: { title: 'BLOG -用户登录' },

    },
    {
        path: '/gotopay',
        name: 'gotopay',
        component: () => import(/* webpackChunkName: "about13" */ '../views/gotopay.vue'),
        meta: { title: 'BLOG -跳转支付' },

    },
    {
        path: '/gotowechat',
        name: 'gotowechat',
        component: () => import(/* webpackChunkName: "about14" */ '../views/gotowechat.vue'),
        meta: { title: 'BLOG -跳转微信登录' },

    },
    {
        path: '/url_scan',
        name: '扫描二维码',
        component: () => import(/* webpackChunkName: "about14" */ '../views/url_scan.vue'),
        meta: { title: 'BLOG -扫码授权' },

    },
    {
        path: '/wechatnow',
        name: 'wechatnow',
        meta: { title: 'BLOG -wechatnow' },
        redirect: 'login',
    },
    {
        path: '/alipay_account',
        name: 'alipay_account',
        component: () => import(/* webpackChunkName: "about15" */ '../views/alipay_account.vue'),
        meta: { title: 'BLOG -跳转支付' },

    }
]

// 前端登录跳转
export const allowedPaths = ['/login', '/product','/home','/login3','/url_scan','/gotowechat','/gotopay','/user/otheruserinfo'
    ,'/friend','/guidang','/about','/user/otheruserinfo',
];

const router = new VueRouter({
    routes,
    mode: 'history',
    base: '/xiba-newblog/'
})

router.onError((error) => {
    // 代码来自于其他人的解决方案，只改了一下正则
    const pattern = /Loading chunk .* failed/g
    const isChunkLoadFailed = error.message.match(pattern)
    if (isChunkLoadFailed) {
        location.reload()
    }
    console.error('Navigation error:', error.message);
})

// 路由守卫？放在路由实例后面
// 跳转前做点事
router.beforeEach((to, from, next) => {
    // 提醒自己不要死循环
    console.log(to.path)
    console.log(from.path)
    console.log(store.state.IsLogin)
    console.log("不要无限跳转")
    if(to.path!==from.path)store.state.zhezhao_show = true

    // 设定标题
    if (to.meta.title) {
        document.title = to.meta.title
    }

  
    // 首次路径保存
    if (!store.state.IsLogin && !allowedPaths.includes(to.path)&& to.path!=='/404'&& to.path!=='/404msg=未找到') {
        sessionStorage.setItem('redirectPath', to.fullPath);
        console.log("保存:"+to.fullPath)
    }
  
    // login
    if(!store.state.IsLogin && !allowedPaths.includes(to.path)){
        console.log("未登录")
        next('/login')
    }
  

    //404
    const { resolved } = router.resolve(to.path)
    if (resolved.matched.length) {
    //console.log("顺利下一跳")
        next()
    } else {
        store.state.zhezhao_show = false
        next('/404') // 没有符合的就404
    }
}) 

// 在完成路由跳转后执行一些逻辑
router.afterEach((to, from) => {

    // 示例：输出一条日志
    console.log(`Navigated from ${from.path} to ${to.path}`);
  
    // 在这里可以执行其他需要在路由跳转后进行的操作
});


export default router