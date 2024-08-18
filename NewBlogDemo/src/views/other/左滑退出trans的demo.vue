<!-- eslint-disable vue/multi-word-component-names -->
<template>
    <div>
  
      <transition :name="transitionName()" mode="out-in">
          <router-view/>
      </transition>
  
    </div>
  </template>
  
<script>

export default {
    components: {
    },
    data() {
        return{
        }
    },
    methods:{
        transitionName() {
            const routeName = this.$route.name;
            if (routeName === 'userinfo') {
                return 'slide-left';
            } else if (routeName === 'option') {
                return 'slide-right';
            } else {
                return '';
            }
        }
    }
}
</script>
  
<style>
/* Home页面的效果: 右进左出 */
.slide-right-enter-active, .slide-right-leave-active {
transition: all 0.3s ease;
}
.slide-right-enter {
transform: translateX(100%);
opacity: 0;
}
.slide-right-leave-to {
transform: translateX(-100%);
opacity: 0;
}

/* About页面的效果: 左进右出 */
.slide-left-enter-active, .slide-left-leave-active {
transition: all 0.3s ease;
}
.slide-left-enter {
transform: translateX(-100%);
opacity: 0;
}
.slide-left-leave-to {
transform: translateX(100%);
opacity: 0;
}

/* 其他页面的效果: 淡入淡出 */
.fade-enter-active, .fade-leave-active {
transition: opacity 0.3s ease;
}
.fade-enter, .fade-leave-to {
opacity: 0;
}
</style>