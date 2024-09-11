import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
    state: {
        IsLogin : true, //登录状态 // 不为true会在初始界面屏闪
        UserName:null,
        UserId:null,
        MIN_WIDTH:"1200px", // 屏幕最小宽度
        CURRENT_WIDTH:null, // 当前屏幕宽度
        CURRENT_HEIGHT:null,
        IsMobile:false,     // 判断是否是手机
        PAGE_STATE:'Tabbar',// 下边栏状态
        kefu_name:"未指定", // 随机到的客服名字
        zhezhao_show:false, // index页面的遮罩
        fanorguanzhu_name:'', // 标题显示是粉丝列表还是关注列表
        userchat_name:'', // 标题显示和谁聊天
        userchat_id:'', // 和谁聊天的用户id
        chat_badge:'', // 未读消息数量
        light_svg_show:true,
        option:{
            show_product_scale_win:false // 是否显示product的缩放调节窗
        }

    }
})