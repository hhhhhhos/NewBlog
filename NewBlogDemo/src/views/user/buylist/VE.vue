<template>
  <div>
    
    <div v-if="!this.$store.state.IsMobile">
      <h1>用户购物车</h1>
      <div style="margin: 50px 100px 150px 100px;" class="myborder">
        <ElTable_buylist @SelectRow="selectrow" :columns="columns1" :geturl="geturl"/>  
      </div>
      <div style="position: fixed; bottom: 0; left: 0; right: 0; margin:  0; padding: 10px 0; display: flex; justify-content: right; background-color: white; z-index: 1000;" class="myborder2">
        <div style="margin: 8px 20px 0 200px;font-weight:bolder;font-size: larger;">合计：<span style="color:rgba(128, 128, 128, 0.844);">¥</span> <span style="color: rgba(255,80,0);">{{ TotalPrice }}</span></div>
        <el-button type="danger" round style="margin:0 100px 0 50px;" @click="gotoresult">结算</el-button>
      </div>
    </div>

    <div v-else>
      
      <div>
        <ElTable_buylist @SelectRow="selectrow" :columns="columns2" :geturl="geturl"/>  
      </div>
      
      <div style="position: fixed; bottom: 0; left: 0; right: 0; margin:  0; padding: 10px 0; display: flex; justify-content:  space-between; background-color: white; z-index: 1000;" class="myborder2">
        <div style="margin: 0;font-weight:bolder;font-size: larger;">
          <div style="text-align: left;margin-left: 20px;">
            <div style="margin-top: 4px;color:red;font-size: larger;font-weight: bold;"><span style="color:black;">合计：</span><span style="margin:-5px 3px 0 0;font-size: large;">¥</span>{{ TotalPrice }}</div>
          </div>
        </div>
        <el-button type="danger" round style="margin-right: 15px;background-color: red;" @click="gotoresult">结算</el-button>
      </div>


    </div>


</div>
</template>

<script>
import ElTable_buylist from '/src/components/ElTable_buylist'

export default {
  components: {
    ElTable_buylist
  },
  data() {
    return{
      columns1 : [
        //{ prop: 'id', label: 'ID', width: '100' },
        //{ prop: 'user_id', label: '用户ID', width: '100' },
        //{ prop: 'product_id', label: '商品ID', width: '100' },
        { prop: 'photo', label: '商品图', width: '180' },
        { prop: 'name', label: '商品名' },
        { prop: 'product_num', label: '购买数量' , width: '120' },
        { prop: 'price', label: '价格', width: '100' },
        { prop: 'create_time', label: '创建时间', width: '180' },
        { label: '操作' , width: '180'}
      ],
      columns2 : [
        //{ prop: 'id', label: 'ID', width: '100' },
        //{ prop: 'user_id', label: '用户ID', width: '100' },
        //{ prop: 'product_id', label: '商品ID', width: '100' },
        { prop: 'photo', label: '商品图', width: '100' },
        //{ prop: 'name', label: '商品名' },
        { prop: 'product_num', label: '信息'  },
        //{ prop: 'price', label: '价格', width: '100' },
        //{ prop: 'create_time', label: '创建时间', width: '180' },
        { label: '操作' , width: '100'}
      ],
      geturl:'/buylist/page',
      TotalPrice:0,
      Datas:[]
    }
  },
  methods:{
    // 根据emit更新价格
    selectrow(datas){
      this.TotalPrice = 0
      this.Datas = datas
      datas.forEach(data=>{
        this.TotalPrice += data.product.price*data.buylist.product_num
        this.TotalPrice = parseFloat(this.TotalPrice.toFixed(2));
      })
    },
    // 跳转结算页
    gotoresult(){
      console.log(this.Datas)
      // 检查有没有卖完的商品
      if(this.Datas.some(data=>data.product.num===0))return this.$message.error("不能选择卖完的商品")
      
      if(this.TotalPrice===0)return this.$message.error("未选择商品")

      console.log("跳转结算页")
      console.log(this.Datas)
      this.$router.push({name:"userbuylist_result",params:{datas:this.Datas}})
    }
  }
}
</script>

<style scoped>
.myborder2 {
  border-radius: 5px;
  border: 5px solid white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, .12), 0 0 6px rgba(0, 0, 0, .04);
}
</style>

