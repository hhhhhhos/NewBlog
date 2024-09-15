import Vue from 'vue';
import ElementUI from 'element-ui';
import App from './App.vue';
import router from './router'
import store from './store'
import bootstrapvue from 'bootstrap-vue'
import moment from 'moment'
moment.locale('zh-cn');
import { Toast } from 'vant';

import 'element-ui/lib/theme-chalk/index.css';
import 'vant/lib/index.css';


Vue.use(bootstrapvue)
Vue.use(ElementUI);

import mavonEditor from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'
Vue.use(mavonEditor)

// 添加一个全局方法
// 新窗口打开
Vue.prototype.$openURL = function (url) {
    console.log(url)
    window.open(url)
};

// 打开网页2 页内
Vue.prototype.$openURL2 = function (url) {
    window.location.href = url
};
Vue.prototype.$openProduct = function (id) {
    this.$router.push(`/product?id=${id}`)
};

// 个人资料显示的上次在线时间
Vue.prototype.$FormDate = function (date) {
    if (!date) return '未知' // 防空报错
    const now = moment()
    const inputDate = moment(date)

    const diffInSeconds = now.diff(inputDate, 'seconds')
    const diffInMinutes = now.diff(inputDate, 'minutes')
    const diffInHours = now.diff(inputDate, 'hours')
    const diffInDays = now.diff(inputDate, 'days')

    if (diffInSeconds < 60) {
        return `在线`
    } else if (diffInMinutes < 60) {
        return `${diffInMinutes}分钟前`
    } else if (diffInHours < 24) {
        return `${diffInHours}小时前`
    } else if (diffInDays < 30) {
        return `${diffInDays}天前`
    } else {
        return `30天前`
    }
}

// 聊天显示的时间
Vue.prototype.$formatDate = function (date) {
    //var date = "2024-05-20T05:04:57.000+00:00"
    const now = moment().startOf('day'); // 获取当前日期并清除时分秒，保持当天开始的时间
    const givenDate = moment(date).startOf('day'); // 将输入的日期字符串转化为日期并清除时分秒
    const diffDays = now.diff(givenDate, 'days'); // 计算两个日期之间的天数差
  
    if (diffDays === 0) {
        return moment(date).format('HH:mm'); // 如果日期相同，返回具体时间
    } else if (diffDays === 1) {
        return `昨天 ${moment(date).format('HH:mm')}`; // 如果日期差是1天，返回昨天和具体时间
    } else if (now.isoWeek() === givenDate.isoWeek()) {
    // 本周内
        return `${moment(date).format('dddd HH:mm')}`;
    } else if (now.year() === givenDate.year()) {
    // 今年内但已超过一周
        return moment(date).format('MM月DD日 HH:mm');
    } else {
    // 超过一年
        return moment(date).format('YYYY年MM月DD日 HH:mm');
    }
}

// error消息
Vue.prototype.$fail = function (msg) {
    Toast.fail(msg)
}

Vue.prototype.$alert = function (msg) {
    Toast(msg)
}

const vue = new Vue({
    router,
    store,
    el: '#app',
    render: h => h(App),
});

export default vue