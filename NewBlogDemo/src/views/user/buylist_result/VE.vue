<template>
  <div>
    
    <div v-if="!this.$store.state.IsMobile">
    <h1>购物车结算</h1>
    <div style="margin: 50px 100px;" class="myborder">
      <h3 style="text-align:left;margin-left:30px;">确认收货信息</h3> 
      <E2 ref="address" style="text-align:left;margin-left:20px;margin-bottom:20px;" @address="address_change" @radio="radio=>radio_change(radio)"></E2>
    </div>
    <div style="margin: 50px 100px 150px 100px;" class="myborder">
      <h3 style="text-align:left;margin-left:30px;">确认订单信息</h3> 
      <div style="margin: 0 30px;">
        <ET1 :Datas="Datas" :columns="columns1" ></ET1>
      </div>
    </div>

    <div style="position: fixed; bottom: 0; left: 0; right: 0; margin:  0; padding: 10px 0; display: flex; justify-content: right; background-color: rgb(248, 248, 248); z-index: 1000;" class="myborder2">
      <div style="margin: 8px 20px 0 200px;font-weight:bolder;font-size: larger;">合计：<span style="color:rgba(128, 128, 128, 0.844);">¥</span> <span style="color: rgba(255,80,0);">{{ TotalPrice }}</span>
        <div style="color: gray;margin: 10px 0 0 5px;display: block;font-weight:normal;font-size:small;display: flex;justify-content:left;">
          送至：{{ this.addresses.length?"".concat(addresses[radio].info).concat(",").concat(addresses[radio].detail):"error"}}
          收件人：{{ this.addresses.length?addresses[radio].name:"error" }}
          电话：{{ this.addresses.length?addresses[radio].phone:"error" }}
        </div>
      </div>
      <el-button type="danger" round style="margin:0 100px 0 50px;" @click="gotopay">提交订单</el-button>
    </div>

    <!-- 弹出框 -->
    <el-dialog :title="this.dialog_title" :visible.sync="dialogVisible" width="300px" center>
      <div style="display: flex; justify-content: center; align-items: center;">
        <el-radio v-model="radio2" label="1">余额(剩余:{{ this.obj.money }})</el-radio>
        <el-radio v-model="radio2" label="2">支付宝</el-radio>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="final_pay">确定</el-button>
      </span>
    </el-dialog>

    
    </div>

    <div v-else style="background-color:rgba(247,248,250);">

      <div style="margin: 10px;padding: 10px;background-color: white;" class="myborder">
        <h3 style="text-align:left;margin: 0;">确认收货信息</h3> 
        <E2 ref="address" style="text-align:left;max-height: 20vh;overflow-x: scroll;margin-top: 20px;" @address="address_change" @radio="radio=>radio_change(radio)"></E2>
      </div>

      <div style="margin: 10px;padding: 10px;background-color: white;" class="myborder">
        <h3 style="text-align:left;margin:0;">确认订单信息</h3> 
        <div style="margin: 0 ;">
          <ET1 :Datas="Datas" :columns="columns2" ></ET1>
        </div>
      </div>


      <div style="position: fixed; bottom: 0; left: 0; right: 0; margin:  0; padding: 10px 0; display: flex; justify-content:  space-between; background-color: white; z-index: 1000;" class="myborder2">
        <div style="margin: 0;font-weight:bolder;font-size: larger;">
          <div style="text-align: left;margin-left: 20px;">
            <div style="margin-top: 4px;color:red;font-size: larger;font-weight: bold;"><span style="margin:5px 3px 0 0;font-size: medium;">¥</span>{{ TotalPrice }}</div>
          </div>
        </div>
        <el-button type="danger" round style="margin-right: 15px;" @click="gotopay">提交订单</el-button>
      </div>

      <!-- 弹出框 -->
      <el-dialog :title="this.dialog_title" :visible.sync="dialogVisible" width="300px" center>
        <div style="display: flex; justify-content: center; align-items: center;">
          <el-radio v-model="radio2" label="1">余额(剩余:{{ this.obj.money }})</el-radio>
          <el-radio v-model="radio2" label="2">支付宝</el-radio>
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="final_pay">确定</el-button>
        </span>
      </el-dialog>



    </div>

  </div>

</template>

<script>
import E2 from '@/components/ElAddress2.vue'
import ET1 from '/src/components/ElTable_buylist_result'
import axios from '@/utils'

export default {
  components:{
    E2,
    ET1
  },
  data() {
    return{
      Datas:[],
      columns1 : [
        //{ prop: 'id', label: 'ID', width: '100' },
        //{ prop: 'user_id', label: '用户ID', width: '100' },
        //{ prop: 'product_id', label: '商品ID', width: '100' },
        { prop: 'photo', label: '商品图', width: '180' },
        { prop: 'name', label: '商品名' },
        { prop: 'product_num', label: '购买数量' , width: '120' },
        { prop: 'price', label: '价格', width: '100' },
        { prop: 'create_time', label: '创建时间', width: '180' },
      ],
      columns2 : [
        //{ prop: 'id', label: 'ID', width: '100' },
        //{ prop: 'user_id', label: '用户ID', width: '100' },
        //{ prop: 'product_id', label: '商品ID', width: '100' },
        { prop: 'photo', label: '商品图', width: '100' },
        { prop: 'product_num', label: '数量' ,align:'center' },
        { prop: 'price', label: '价格', width: '120' ,align:'center'},
      ],
      TotalPrice:0,
      radio:0,
      addresses:[],
      dialogVisible:false,
      dialog_title:"支付方式",
      radio2:"1",
      dialogdata:
      {
        "info": "",
        "info_code":"",
        "name": "",
        "phone": "",
        "detail": "",
        "is_default":""
      },
      obj:{
        "id": null,
        "name": null,
        "age": null,
        "sex": null,
        "addresses": null,
        "phone": null,
        "create_time": Date,
        "password": null,
        "money":null
      },
      alipay:{
        "id":null,
        "money":null,
        "num":null,
        "name":null
      }
      //ids:null
    }
  },
  methods:{
    // 选择地址
    radio_change(radio){
      console.log(radio)
      this.radio = radio
    },
    address_change(val){
      console.log(val)
      this.addresses = JSON.parse(JSON.stringify(val))
      console.log(this.addresses[this.radio].info)
    },
    // 点提交订单
    gotopay(){

      if(!this.addresses.length)return this.$message.error("收货地址不能为空")
      this.dialogVisible = true
    },
    // 点确定支付
    async final_pay(){
      console.log(this.radio2)
      // 如果创建历史订单信息成功
      if(await this.build_historylist()){
        // 判断跳转余额支付还是支付宝支付 this.radio2=1 余额 2支付宝
        if(this.radio2==="1"){
          // 余额支付
          await this.moneypay(this.alipay.id)
          // 无论支付·结果 都跳转历史交易页
          setTimeout(() => {this.$router.push('/user/historylist')}, 1000);
        }else{
          // 支付宝支付
          console.log("支付宝支付")
          // 不拦截支付
          sessionStorage.setItem('StopRedirectPay',"false")
          setTimeout(() => this.$router.push(`/alipay_account?id=${this.alipay.id}&money=${this.alipay.money}&num=${this.alipay.num}&name=${this.alipay.name}`), 1000);
        }
      }
      else{
        //this.$message.error("订单创建失败")
      }
      this.dialogVisible = false
    },
    // 创建历史订单信息
    async build_historylist(){
      var result = false
      
      // #region 先判断满不满足建单条件
      if(this.TotalPrice <= 0){
          this.$message.error("总金额异常, 不能为0")
          return result
      }
       // 如果选余额支付
      if(this.radio2==="1"){
        if(this.obj.money < this.TotalPrice){
          this.$message.error("余额不足，无法支付")
          return result
        }
      }
      // #endregion

      // .....待完善
      // 根据ids删除 请求范例
      if(this.Datas?.[0].buylist.id === null && this.Datas.length === 1){
        await axios.post('/user/build/order_for_one',{
              "buylistDtoLists":this.Datas,
              "address":this.$refs.address.obj.addresses[this.$refs.address.radio]
            })
            .then(response=>{
              if(response.data.code){
                this.$message.success(response.data.data)
                this.alipay = response.data.map.alipay
                //setTimeout(() => {window.location.reload()}, 500);
                result =  true
              }
              else {
                this.$message.error(response.data.msg)
                console.log(response)
                result =  false
              }
            }).catch(error=>{
              this.$message.error(error.data.msg);
              console.log(error)
              result =  false
            })

      }else{
        await axios.post('/user/build/order',{
              "buylistDtoLists":this.Datas,
              "address":this.$refs.address.obj.addresses[this.$refs.address.radio]
            })
            .then(response=>{
              if(response.data.code){
                this.$message.success(response.data.data)
                this.alipay = response.data.map.alipay
                //setTimeout(() => {window.location.reload()}, 500);
                result =  true
              }
              else {
                this.$message.error(response.data.msg)
                console.log(response)
                result =  false
              }
            }).catch(error=>{
              this.$message.error(error.data.msg);
              console.log(error)
              result =  false
            })
      }
      
      // 先去后端验证吧 判断是余额支付 还是支付宝 成功后再在后端创建订单
      return result
    },
    async getuserinfo(){
      await axios.get('/user/info')
      .then(response=>{
        //if(response.data.code)this.$message.success("获取成功")
        //else this.$message.error("获取失败："+response.data.msg)
        this.obj = response.data.data
        if(this.obj.addresses===null) this.obj.addresses=[]
        else{
          // 默认地址置顶
          for(var i=0;i<this.obj.addresses.length;i++){
            if(this.obj.addresses[i].is_default){
              const temp = this.obj.addresses[0]
              this.obj.addresses[0] = this.obj.addresses[i]
              this.obj.addresses[i] = temp
            }
          }
        }
        return this.obj
      }).catch(error=>{
        console.log(error)
        this.$message.error("获取失败："+error.data.msg)
        return this.obj
      })
    },
    // 余额支付
    async moneypay(order_id){
      console.log("余额支付")
      var result = false
      await axios.post(`/order/payonmoney?order_id=${order_id}`)
      .then(response=>{
        if(response.data.code){
          this.$message.success(response.data.data)
          //setTimeout(() => {window.location.reload()}, 500);
          result =  true
        }
        else {
          this.$message.error(response.data.msg)
          console.log(response)
          result =  false
        }
      }).catch(error=>{
        this.$message.error(error.data.msg);
        console.log(error)
        result =  false
      })
      return result
    }
  },
  mounted() {
    this.$store.state.zhezhao_show = false
    console.log(this.Datas)
    this.getuserinfo()
  },
  created(){
    if(this.$route.params.datas){
      this.Datas =  JSON.parse(JSON.stringify(this.$route.params.datas))
      this.Datas.forEach(data=>{
        this.TotalPrice += data.product.price*data.buylist.product_num
        this.TotalPrice = parseFloat(this.TotalPrice.toFixed(2));
      })
    }
    else this.$alert('未获取到购物车信息','error')
  }
}
</script>

