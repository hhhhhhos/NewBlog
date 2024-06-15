<template>
  <div>

    <!-- 无限滚动 -->
    <van-list
        v-model="loading_vanlist"
        :finished="finished"
        finished-text="没有更多了"
        @load="debouncedOnLoad"
        offset=0
        >

        <div v-for="(column, index) in tableData"
        :key="index">
          
          <van-cell style="text-align: left;" :title="column.send_json.messages[1].content.substring(0,10)" :value="column.kefu_name" size="large" :label="column?.create_time?.replace('T',' ')" 
          is-link :to="`/kefu?chat_id=${column.id}`" />

        </div>

    </van-list>

  </div>
</template>

<script>
import axios from '@/utils'
import { List} from 'vant';
import { throttle } from 'lodash';
import { Cell } from 'vant';

export default {
  components:{
    'van-list':List,
    'van-cell':Cell,
  },
  data() {
    return{
      tableData: [],
      total: 0, //总个数
      current:0, // 当前页数
      pages:0, // 总页数
      loading_vanlist:false,
      finished: false,
      currentPage:0,
      PageSize:8,
      debouncedOnLoad:null
    }
  },
  methods:{

    // vant手机划到底部时触发
    async onLoad() {
      //var oldScrollPosition
      console.log("滚到底部，触发加载")
      // 异步更新数据
      // setTimeout 仅做示例，真实场景中一般为 ajax 请求
      this.currentPage +=1
      await axios.get('kefu/page',{
        params: {
          currentPage: this.currentPage,
          PageSize: this.PageSize,
        }
      }).then(response=>{
        if(response.data.code){
          this.tableData.splice(this.tableData.length, 0, ...response.data.data.records);
          this.total = response.data.data.total
          this.current = response.data.data.current
          this.pages = response.data.data.pages
        }else{
          this.$message.error(response.data.msg)
        }

        console.log(response)
      }).catch(error=>{
        console.log(error)
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
  mounted(){
    this.$store.state.zhezhao_show = false
  },
  created(){
    // 节流OnLoad 1._.throttle 每n秒钟只调用一次 2._.debounce n秒之后无操作 调用
    this.debouncedOnLoad = throttle(this.onLoad, 1000);
  }
}
</script>

<style scoped>

</style>
