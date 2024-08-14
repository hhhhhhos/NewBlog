<template>
  <div>
    <h3>我关注的</h3>
    <div class="myborder myfitcontent big">
        <div class="out-container">
            <div v-for="i in 5" :key="i" class="item">
                <FC quote="我是傻逼我是傻逼我是傻" username="傻逼" 
                avatarUrl="https://q1.qlogo.cn/g?b=qq&nk=952666016&s=100"/>
            </div>
            <div v-for="i in 2" :key="i" class="item">
                <FC quote="我是傻逼我是傻逼我是傻我是傻逼我是傻逼我是傻我是傻逼我是傻逼我是傻傻逼我是傻我是傻逼我是傻逼我" username="傻逼我是傻逼我是傻逼我是傻我是傻逼我是傻逼我是傻我是傻逼我是傻逼我是傻" 
                avatarUrl="https://q1.qlogo.cn/g?b=qq&nk=952666016&s=100"/>
            </div>
        </div>
    </div>

    <h3>关注我的</h3>
    <div class="myborder myfitcontent big">
        <div class="out-container">
            <div v-for="i in 5" :key="i" class="item">
                <FC quote="我是傻逼我是傻逼我是傻" username="傻逼" 
                avatarUrl="https://q1.qlogo.cn/g?b=qq&nk=952666016&s=100"/>
            </div>
            <div v-for="i in 2" :key="i" class="item">
                <FC quote="我是傻逼我是傻逼我是傻我是傻逼我是傻逼我是傻我是傻逼我是傻逼我是傻傻逼我是傻我是傻逼我是傻逼我" username="傻逼我是傻逼我是傻逼我是傻我是傻逼我是傻逼我是傻我是傻逼我是傻逼我是傻" 
                avatarUrl="https://q1.qlogo.cn/g?b=qq&nk=952666016&s=100"/>
            </div>
        </div>
    </div>

    <pinglun pinglun_id="45698"/>
  </div>
</template>

<script>
import FC from '@/components/friendcard'
import pinglun from '@/views/product/VE.vue'

export default {
    components:{
        FC,
        pinglun
    },
    data() {
        return{
        }
    },
    methods:{
    },
    created(){
    },
    mounted() {
        this.$store.state.zhezhao_show = false
    },
    beforeDestroy(){
    }
}
</script>

<style scoped>
@media (min-width: 762px) {
    .out-container {
        margin: 0 auto;
        width: 662px;
        display: flex;
        flex-wrap: wrap; /* 允许换行 */
    }
}
@media (min-width: 1092px) {
    .out-container {
        margin: 0 auto;
        width: 992px;
        display: flex;
        flex-wrap: wrap; /* 允许换行 */
    }
}

@media (min-width: 1432px) {
    .out-container {
        margin: 0 auto;
        width: 1332px;
        display: flex;
        flex-wrap: wrap; /* 允许换行 */
    }
}


.item {
    flex: 0 0 310.66px; /* 设置每个项目的宽度为30%，根据你希望每行显示的项目个数调整 */
    margin: 10px;
    box-sizing: border-box; /* 确保padding和border不会影响元素的宽度 */
    border: 1px solid var(--ptext-color);
    border-radius: 5px;
    background-color: var(--product-bg);
}

.item:hover{

    filter: brightness(0.95);
}

.big{
    margin-bottom: 20px;
}

h3{
    text-align: left;
    margin-left: 100px;
}

</style>
