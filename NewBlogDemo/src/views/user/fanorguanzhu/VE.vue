<template>
  <div>
    <h1 v-if="false">{{this.$store.state.fanorguanzhu_name}}</h1>
    <!-- 无限滚动 -->
    <van-list
        v-model="loading_vanlist"
        :finished="finished"
        finished-text="没有更多了"
        @load="debouncedOnLoad"
        offset=0
        >
        <div v-for="(column, index) in this.tableDatas" :key="index">
          
          <div class="user_card" @click="column.id === $store.state.UserId?$router.push(`/user/info`):$router.push(`/user/otheruserinfo?user_id=${column.id}`)">
            
            <img
              style="margin: 20px 0 25px 25px;cursor: pointer;width:70px;height: 70px;border-radius: 90px;border: 2px solid white;"
              :src="column?.wechat_headimgurl?column.wechat_headimgurl:target_img_src"
              
            >
            <div style="display: flex;justify-content: left;align-items: center;">
              <span style="margin: 0 0 10px 20px;">{{ column.wechat_nickname?column.wechat_nickname:column.name }}</span>
              <img style="margin: 0 0 10px 10px;" v-if="column?.wechat_nickname?true:false" width="18px" height="18px" :src="require('@/assets/wechat_confirm.png')">
            </div>

          </div>

        </div>

    </van-list>
  </div>
</template>

<script>
import axios from '@/utils';
import { List} from 'vant';
import { throttle } from 'lodash';

export default {
    components: {
        'van-list':List,
    },
    data() {
        return{
            // 头像
            target_img_src:require('@/assets/default_headimg5.webp'),
            //
            user_id:null,
            tableDatas:[],
            // 分页参数
            currentPage:0,
            PageSize:10,
            total:null,
            current:null,
            pages:null,
            // 无限列表
            loading_vanlist:false,
            finished: false,
            //
            debouncedOnLoad:null,
        }
    },
    methods:{
        async test(){
      
        },
        async OnLoad(){
            console.log("滚到底部，触发加载")

            this.currentPage +=1

            await axios.get(`/fan/page`,{
                params: {
                    p1:this.$route.params.segment1,
                    user_id:this.user_id,
                    currentPage: this.currentPage,
                    PageSize: this.PageSize,
                }
            })
                .then(response=>{
                    if(response.data.code){
                        // this..
                        this.tableDatas = response.data.data.records
                        //
                        this.total = response.data.data.total
                        this.current = response.data.data.current
                        this.pages = response.data.data.pages
                    }
                    else this.$message.error("获取失败："+response.data.msg)
                }).catch(error=>{
                    console.log(error)
                    this.$message.error("获取失败："+error.data.msg)
                })

            // 加载状态结束
            this.loading_vanlist = false;

            // 当前页数大于等于总页数
            //if (this.PageSize >= this.TotalPage) {
            if (this.current >= this.pages) {
                this.finished = true;
            }

        },
    },
    created(){
        // 节流OnLoad 1._.throttle 每n秒钟只调用一次 2._.debounce n秒之后无操作 调用
        this.debouncedOnLoad = throttle(this.OnLoad, 1000);
    },
    mounted(){
        this.$store.state.zhezhao_show = false
        this.user_id = this.$route.params.segment2
        const p1 = this.$route.params.segment1
        console.log(this.$route.params.segment2)
        console.log(this.$route.params.segment1)
        if(p1==='fs'){
            this.$store.state.fanorguanzhu_name = "粉丝列表"
        }else if(p1==='gz'){ 
            this.$store.state.fanorguanzhu_name = "关注列表"
        }else{
            alert("参数错误")
            return
        }

        this.test()
    }
}
</script>

<style scoped>
.user_card{
  display: flex;
  background-color: white;
  margin-top: 2px;
  cursor: pointer;
}
</style>
