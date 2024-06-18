<template>
  <div v-loading="isloading1">
    <!-- 弹出框 -->
    <el-dialog :title="this.dialog_title+this.dialogindex" :visible.sync="dialogVisible" width="40%" >
      <el-form ref="form" :model="dialogdata" label-width="80px" >
        <el-form-item label="所在地区">
          <ElA style="float:left;" :PselectedOptions="dialogdata.info_code" @info="info=>{dialogdata.info=JSON.parse(JSON.stringify(info))}" @info_code="info_code=>{dialogdata.info_code=JSON.parse(JSON.stringify(info_code))}"></ElA>
        </el-form-item>
        <el-form-item label="详细地址">
          <el-input v-model="dialogdata.detail"></el-input>
        </el-form-item>
        <el-form-item label="收件人">
          <el-input v-model="dialogdata.name"></el-input>
        </el-form-item>
        <el-form-item label="电话号码">
          <el-input v-model="dialogdata.phone"></el-input>
        </el-form-item>
        <el-form-item label="是否默认">
          <el-switch
            v-model="dialogdata.is_default"
            active-text="是"
            inactive-text="否"
            style="float:left;margin-top: 10px;">
          </el-switch>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="confirm_nochange_address">取消</el-button>
        <el-button type="primary" @click="confirm_change_address">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
// 主要用在buylist

import ElA from '/src/components/AddressAdd/ElAddress'

export default {
  props: {
    //state:String
  },
  components: {
    ElA
},
  data() {
    return{
      isloading1:true,
      datas:[],
      obj:{
        "id": null,
        "name": null,
        "age": null,
        "sex": null,
        "addresses": [],
        "phone": null,
        "create_time": Date,
        "password": null
      },
      isupdateinfo:false,
      dialogVisible:false,
      dialog_title:"",
      dialogdata:
      {
        "info": [],
        "info_code":[],
        "name": "",
        "phone": "",
        "detail": "",
        "is_default":false
      },
      dialogindex:-1,
      radio:0
    }
  },
  methods:{
    // 点击展开框的确认
    confirm_change_address(){
      if(this.dialogdata.info.length===0){
        this.$message.error("未选择地址")
        return
      }
      this.$emit("address",this.dialogindex,this.dialogdata)
      console.log("子地址组件")
      console.log(this.dialogindex)
      console.log(this.dialogdata)
      this.dialogVisible = false
    },

    // 点击展开框的取消
    confirm_nochange_address(){

      this.dialogVisible = false
    }
  },
  mounted(){
      this.isloading1 = false

  },
  watch:{
  }
}
</script>

<style scoped>
.myhover:hover{
  background-color: rgb(247,248,255);
}
</style>
