<template>
  <div class="app-container"> 
    <div style="margin: 0 0 20px 0;">
      <el-button type="primary" plain @click="OpenDialog('删除订单')">删除</el-button>
      <el-button type="primary" plain @click="OpenDialog('查询订单')">查询</el-button>
      <el-tag
        style="margin:10px 10px 0 20px;padding-left: 10px;"
        :disable-transitions=true
        v-for="tag in tags"
        :key="tag.name"
        closable
        @close="removeTag(tag.key)"
        :type="tag.type">
        {{tag.name}}
      </el-tag>
    </div>

    <el-table
      v-loading="IsTableLoading"
      :data="tableData"
      border fit highlight-current-row
      @selection-change="handleSelectionChange"
      style="width:100%;">

      <el-table-column
        type="selection"
        align="center"
        width="40">
      </el-table-column>

      <el-table-column
          v-for="(column, index) in this.columns"
          :key="index"
          :prop="column.prop"
          :label="column.label"
          :width="column.width"
          :align="column.align"
          >
          <template slot-scope="scope" >
            <div v-if="column.label==='访问时间'">{{ tableData[scope.$index][column.prop]?tableData[scope.$index][column.prop].replace(/T/g, ' '):null }}</div>
            <div v-else-if="column.label==='姓名'">{{ isValidUUID(tableData[scope.$index][column.prop])?'UUID值':tableData[scope.$index][column.prop] }}</div>
            <div v-else-if="column.label==='用户名'">
              {{ tableData[scope.$index][column.prop]?
              isValidUUID(tableData[scope.$index][column.prop])?"UUID值":tableData[scope.$index][column.prop]
              :"未登录"}}
            </div>
            <div v-else>
              {{ tableData[scope.$index][column.prop]}}
            </div>
          </template>
      </el-table-column>

    </el-table>

    <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[5, 10, 15, 20]"
          :page-size=PageSize
          layout="total, sizes, prev, pager, next, jumper"
          :total="TotalPage"
          style="margin-top: 20px;">
    </el-pagination>

    <!-- 弹出框1 (增改) -->
    <el-dialog :title="this.dialog_title2" :visible.sync="dialogVisible2" width="60%" >
      
      <el-form  style="margin:0 20px 0 0;" :rules="rules" label-position="right" ref="form" :model="dialogdata2" label-width="80px">
        <!-- 使用el-row和el-col创建栅格布局 -->
        <el-row :gutter="20">

          <el-col :span="12" v-for="(column, index) in columns" :key="index">
            <span v-if="column.label==='ID'||column.label==='创建时间'||column.label==='版本'||column.label==='图像'" />
            <el-form-item v-else :label="column.label" :prop="column.prop">
              <el-input v-model="dialogdata2[column.prop]"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="图像" prop="photo" >
              <el-upload
                style="margin: 5px 0 0 5px;"
                class="upload-demo"
                ref="upload"
                :show-file-list="true"
                :file-list="fileList"
                action="#"
                :limit="1"
                
                :before-upload="beforeUpload"
                :on-preview="handlePreview"
                :on-remove="handleRemove"
                :on-exceed="handleExceed"
                :http-request="handleFileUpload"
                list-type="picture">
                <el-button size="small" type="primary">点击上传</el-button>
                <div slot="tip" class="el-upload__tip">只能上传jpg/png一张，且不超过500kb</div>
              </el-upload>
            </el-form-item>
          </el-col>

        </el-row>
        
      </el-form>

      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible2 = false,gettable()">取消</el-button>
        <el-button type="primary" @click="confirm_change">确定</el-button>
      </span>
    </el-dialog>

    <!-- 弹出框1-2(地址选择) -->
    <El2 ref="address" style="text-align:left;margin-left:20px;margin-bottom:20px;" @address="address_finish" />


    <!-- 弹出框2（查询用户） -->
    <el-dialog :title="this.dialog_title3" :visible.sync="dialogVisible3" width="60%" >
  
      <el-form  style="margin:0 20px 0 0;" label-position="right" ref="form2" :model="dialogdata3" label-width="80px">
        <el-row :gutter="20">
          <el-col :span="12"  v-for="(column, index) in columns2" :key="index" >
            <!-- 使用el-row和el-col创建栅格布局 -->
              <el-form-item :label="column.label" :prop="column.prop">
                <div style="display: flex;" v-if="column.label==='时间'">
                  <el-date-picker
                    v-model="dialogdata3.startDate"
                    type="datetime"
                    placeholder="选择开始时间">
                  </el-date-picker>
                  <el-date-picker
                    v-model="dialogdata3.endDate"
                    type="datetime"
                    placeholder="选择结束时间">
                  </el-date-picker>
                </div>
                <el-input style="max-width: 250px;" v-else v-model="dialogdata3[column.prop]"></el-input>
              </el-form-item>
          </el-col>
        </el-row>         
      </el-form>

      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible3 = false">取消</el-button>
        <el-button type="primary" @click="gotosearch()">确定</el-button>
      </span>

    </el-dialog>

  </div>
</template>

<script>
import axios from '@/utils/axios';
import El2 from '/src/components/AddressAdd/ElAddress2'


export default {
    components:{
        El2,

    },
    data() {
        return {
        // 表格图片获取路径
            staticPath: process.env.VUE_APP_STATIC_PATH,
            // 图片
            photo:null,
            // 图片(el有bug绑定不上)
            fileList: [
            ],
            //
            tableData: [],
            currentPage:1,
            PageSize:10,
            TotalPage:null,
            IsTableLoading:true,
            columns : [
                { prop: 'create_time', label: '访问时间', width: '160' },
                { prop: 'visitor_name', label: '用户名', width: '80' },
                { prop: 'wechat_nickname', label: '微信名', width: '80' },
                { prop: 'realIp', label: '访问ip', width: '120' },
                { prop: 'city', label: 'ip地点', width: '120' },
                { prop: 'originalURI', label: '访问URL' },
                { prop: 'method', label: '访问方法' , width: '80'},
                { prop: 'forwardedProto', label: '协议' , width: '60'},
                { prop: 'deviceClass', label: '设备', width: '80' },
                { prop: 'deviceBrand', label: '设备品牌', width: '80' },
                { prop: 'deviceFirmwareVersion', label: '版本', width: '80' },
                { prop: 'operatingSystemName', label: '操作系统', width: '120' },
                { prop: 'operatingSystemVersion', label: '版本', width: '60' },
                { prop: 'agentName', label: '浏览器', width: '80' },
                { prop: 'agentVersion', label: '版本', width: '80' },
                { prop: 'user_uuid', label: '访客标识', width: '300' },
            ],
            dialogVisible2:false,
            dialog_title2:"",
            dialogdata2:
        {
            "id": null,
            "name":null,
            "price": null,
            "num": null,
            "photo": null,
            "info":null,
            "type":null,
            "create_time":null,
            "version":null,
        },
            radio:null,
            addresses:[],
            rules: {
                name: [
                    { required: true, message: '请输入姓名', trigger: 'blur' },
                    //{ min: 3, max: 5, message: '长度在 3 到 5 个字符', trigger: 'blur' }
                ],
                price: [
                    { required: true, message: '请输入价格', trigger: 'blur' },
                    //{ min: 3, max: 5, message: '长度在 3 到 5 个字符', trigger: 'blur' }
                    { 
                        validator: (rule, value, callback) => {
                            // 验证是否只包含数字
                            if (!value) {
                                callback(new Error('请输入数字值'));
                            } else if (!/^\d+$/.test(value)) {
                                // 正则表达式 ^\d+$ 用于检查字符串是否只包含数字
                                callback(new Error('只能输入数字'));
                            } else if (value <= 0 || value >= 1000000) {
                                // 验证数字是否大于0且小于1000000
                                callback(new Error('数字必须大于0且小于7位数'));
                            } else {
                                // 如果验证通过
                                callback();
                            }
                        }, 
                        trigger: 'blur'
                    }
                ],
                num: [
                    { required: true, message: '请输入数量', trigger: 'blur' },
                    //{ min: 3, max: 5, message: '长度在 3 到 5 个字符', trigger: 'blur' }
                    { 
                        validator: (rule, value, callback) => {
                            // 验证是否只包含数字
                            if (!value) {
                                callback(new Error('请输入数字值'));
                            } else if (!/^\d+$/.test(value)) {
                                // 正则表达式 ^\d+$ 用于检查字符串是否只包含数字
                                callback(new Error('只能输入数字'));
                            } else if (value <= 0 || value >= 1000000) {
                                // 验证数字是否大于0且小于1000000
                                callback(new Error('数字必须大于0且小于7位数'));
                            } else {
                                // 如果验证通过
                                callback();
                            }
                        }, 
                        trigger: 'blur'
                    }
                ],
            },
            rows_selection:[],
            // 弹窗2（查询弹窗）
            dialogVisible3:false,
            dialog_title3:"查询筛选添加",
            dialogdata3:
        {
            "ip": null,
            "ip_location":null,
            "url":null,
            "method":null,
            "device":null,
            "brand":null,
            "brand_version":null,
            "os":null,
            "os_version":null,
            "browser":null,
            "uuid":null,
            "startDate":null,
            "endDate":null,
        },
            columns2 : [
                { prop: 'ip', label: 'Ip', width: '180' },
                { prop: 'ip_location', label: 'Ip地点', width: '80' },
                { prop: 'url', label: '访问地址', width: '80' },
                { prop: 'method', label: '访问方法', width: '80' },
                { prop: 'device', label: '设备', width: '80' },
                { prop: 'brand', label: '品牌', width: '80' },
                { prop: 'brand_version', label: '品牌版本', width: '80' },
                { prop: 'os', label: '操作系统', width: '80' },
                { prop: 'os_version', label: '系统版本', width: '180' },
                { prop: 'browser', label: '浏览器', width: '80' },
                { prop: 'uuid', label: '访客标识', width: '80' },
                { prop: 'create_time', label: '时间', width: '80' },
                { prop: 'visitor_name', label: '用户名', width: '80' },
                { prop: 'wechat_nickname', label: '微信名', width: '80' },
            ],
            tags: [
            ]
        }
    },
    methods:{
        // 文字是否存在，存在返回
        exist(str){
            if(str!==null)
                return ","+str
            else
                return ""
        },
        // 图片加载失败时改src
        handleImageError(event) {
            console.log("handleImageError")
            console.log(this.staticPath + "deletedproduct.webp")
            // 处理图片加载失败的逻辑
            event.target.src = this.staticPath + "deletedproduct.webp"
        },
        // 修改窗口打开时，初始化图片fileList到web显示（此时this.$refs.upload.uploadFiles?.[0]?.raw为空）
        init_dialogdata2_photo(){
            console.log("this.$refs.upload.uploadFiles?.[0]?.raw")
            console.log(this.$refs.upload?.uploadFiles?.[0]?.raw)
            console.log(this.dialogdata2)
            if(this.dialogdata2.photo!=="noproduct")this.fileList = [{ name: `${this.dialogdata2?.photo}.webp`, url: `${this.staticPath}${this.dialogdata2.photo}.webp` }];
        },
        // 这里上传完图片和beforeUpload一起触发 // 这里上传完才会初始化this.$refs.upload.uploadFiles?.[0]?.raw
        handleFileUpload(file) {
            console.log("submit")
            //console.log(file)
            this.photo = file
            //console.log(this.photo.file)
            console.log(this.$refs.upload.uploadFiles?.[0]?.raw)
        },
        // 图片上传前检查（:auto-upload="false"会使得:before-upload="beforeUpload"被禁止）
        beforeUpload(file) {
            console.log("beforeUpload!")
            const allowedTypes = ['image/jpeg', 'image/png'];
            const isJPGorPNG = allowedTypes.includes(file.type);
            const isLt500KB = file.size / 1024 < 500;

            if (!isJPGorPNG) {
                this.$message.error('只能上传jpg/png文件!');
                return false;
            }
            if (!isLt500KB) {
                this.$message.error('上传文件大小不能超过 500KB!');
                return false;
            }
            return true;
        },
        // 图片移除（数组）
        handleRemove(file, fileList) {
            console.log("remove")
            console.log(file, fileList);
            console.log(this.$refs.upload?.uploadFiles?.[0]?.raw)
            this.dialogdata2.photo = "noproduct"
        },
        // 图片预览（数组）
        handlePreview(file) {
            console.log("preview")
            console.log(file);
        },
        // 图片超出限制
        handleExceed() {
            this.$message.error('只能上传一张图片');
        },
        // 地址组件选完 触发的事件
        address_finish(index,val){
            console.log("父组件")
            console.log(index)
            console.log(val)

            if(this.$refs.address.dialog_title==='新增地址'){
                const address = JSON.parse(JSON.stringify(val))

                // 默认
                if(address?.is_default){
                    this.addresses.forEach(item=>{
                        item.is_default = false
                    })
                }
                this.addresses.push(address)
            }

            else if(this.$refs.address.dialog_title==='更改地址'){
                const address = JSON.parse(JSON.stringify(val))
                console.log(address)
                // 默认
                if(address?.is_default){
                    this.addresses.forEach(item=>{
                        item.is_default = false
                    })
                }

                //this.addresses[index] = JSON.parse(JSON.stringify(val)) 直接改索引 vue不会渲染
                this.$set(this.addresses, index, address);
            }
        
        
            else{
                this.$message.error("走到这一步，说明你写错了")
            }
            console.log(this.addresses)
        

        },
        // 删除地址（数组里某个）
        delete_address(index2){
            console.log(index2)
            this.addresses.splice(index2,1)
        },
        // 新加地址
        new_address(){
            this.$refs.address.dialogdata =
        {
            "info": [],
            "info_code":[],
            "name": "",
            "phone": "",
            "detail": "",
            "is_default":false
        },
            this.$refs.address.dialog_title='新增地址'
            this.$refs.address.dialogVisible=true;
        },
        // 更改地址（数组里某个）// 这里深拷贝
        change_address(index2,data2){
            this.$refs.address.dialogdata = JSON.parse(JSON.stringify(data2))
            this.$refs.address.dialog_title = '更改地址'
            this.$refs.address.dialogindex = index2
            this.$refs.address.dialogVisible=true
        },
        gettable(){
            this.IsTableLoading = true
            axios.get('/user-agent-details/selectbyadmin',{
                params: {
                    currentPage: this.currentPage,
                    PageSize: this.PageSize,
                }
            }).then(response=>{
                if(response.data.code===0)this.$message.error(response.data.msg)
                else {
                    this.tableData = response.data.data.records
                    this.TotalPage = response.data.data.total
                    this.IsTableLoading = false
                    //this.$message.success("获取成功")
                }
            }).catch(error=>{
                this.$message.error(error.data.msg)
                console.log(error)
            })
        },
        // 页容量变化
        handleSizeChange(val) {
            this.PageSize = val
            this.gettablebycondition()
        },
        // 切页
        handleCurrentChange(val) {
            this.currentPage = val
            this.gettablebycondition()
        },
        // 表格列选择
        handleSelectionChange(val){
            console.log(val)
            this.rows_selection = val
        },
        // 点增删改查
        OpenDialog(val){

            if(val==="新增访客记录"){

                // 初始化数据
                this.dialogdata2 = 
          {
              "id": null,
              "name":null,
              "price": null,
              "num": null,
              "photo": null,
              "info":null,
              "type":null,
              "create_time":null,
              "version":null,
          }
          // 初始化验证 // ?.防止this.$refs.form为空报错 为空不运行
          this.$refs.form?.resetFields();

          this.dialog_title2 = val
          this.dialogVisible2 = true
            }

            else if(val==="删除订单"){
                if(this.rows_selection.length===0){
                    this.$message.error("请选择要删除的历史记录")
                    return
                }

                this.$confirm('确定删除这'+(this.rows_selection.length)+'个访客记录吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.delete_byadmin() // 删除用户
                }).catch(() => {
                    this.$message({
                        type: 'info',
                        message: '已取消删除'
                    });          
                });
            }

            else if(val==="修改访客记录"){
                if(this.rows_selection.length===0){
                    this.$message.error("请选择要修改的访客记录")
                    return
                }

                if(this.rows_selection.length!==1){
                    this.$message.error("一次只能修改一个访客记录")
                    return
                }

                // 初始化数据
                this.dialogdata2 = this.rows_selection?.[0]
          // 初始化验证 // ?.防止this.$refs.form为空报错 为空不运行
          this.$refs.form?.resetFields();
          // 初始化标题
          this.dialog_title2 = val
          // 初始化图片
          this.init_dialogdata2_photo()

          this.dialogVisible2 = true

            }

            else if(val==="查询订单"){
                this.dialogVisible3 = true
            }

            else{
                //
            }

        
        },
        // 展开框1的确定
        confirm_change(){

            this.$refs.form.validate(result => {
                if (result) {
                    console.log("验证通过");
                    if(this.dialog_title2==="新增访客记录")
                        this.axios_add()
                    else if(this.dialog_title2==="修改访客记录")
                        this.axios_update()
                    else
                        this.$message.error('err')
                } else{
                    console.log("验证失败");
                }
            })

        },
        // 添加一个
        axios_add(){
        //this.$refs.upload.submit();
            console.log(this.$refs.upload);
            console.log(this.dialogdata2)
            console.log(this.$refs.upload.uploadFiles?.[0]?.raw)
            let formData = new FormData()
            formData.append('photo', this.$refs.upload.uploadFiles?.[0]?.raw)
            formData.append('product_json',JSON.stringify(this.dialogdata2)) // 序列化
            axios.post('/product/addonebyadmin',formData,{
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            })
                .then(response=>{
                    if(response.data.code===0)this.$message.error(response.data.msg)
                    else {
                        this.dialogVisible2 = false
                        this.$message.success("添加成功")
                        this.gettablebycondition()
                    }
                }).catch(error=>{
                    this.$message.error(error.data.msg)
                    console.log(error)
                })
        },
        // 更新一个
        // 新范例 防404报错
        axios_update(){
            console.log(this.dialogdata2)
            console.log(this.$refs.upload)
            let formData = new FormData()
            // photo只有上传了新图片才会有
            formData.append('photo', this.$refs.upload.uploadFiles?.[0]?.raw)
            formData.append('product_json',JSON.stringify(this.dialogdata2)) // 序列化
            axios.put('/product/updateonebyadmin',formData,{
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            })
                .then(response=>{
                    if(response.data.code===0)this.$message.error(response.data.msg)
                    else {
                        this.dialogVisible2 = false
                        this.$message.success("更新成功")
                        this.gettablebycondition()
                    }
                }).catch(error=>{
                    if(error.data?.msg)this.$message.error(error.data.msg)
                    else this.$message.error(error)
                    console.log(error)
                })
        },
        // 删一堆
        delete_byadmin(){
            axios.post('/user-agent-details/deletelistbyadmin',this.rows_selection)
                .then(response=>{
                    if(response.data.code===0)this.$message.error(response.data.msg)
                    else {
                        this.dialogVisible2 = false
                        this.$message.success("删除成功")
                        this.gettablebycondition()
                    }
                }).catch(error=>{
                    if(error.data?.msg)this.$message.error(error.data.msg)
                    else this.$message.error(error)
                    console.log(error)
                })
        },
        // 发起axios条件查询
        gettablebycondition(){
            this.IsTableLoading = true
            var page = {
                currentPage: this.currentPage,
                PageSize: this.PageSize
            }
            var params = {...page,...this.dialogdata3}
        
            axios.get('/user-agent-details/selectbyadmin',{
                params: params
            }).then(response=>{
                if(response.data.code===0)this.$message.error(response.data.msg)
                else {
                    this.tableData = response.data.data.records
                    this.TotalPage = response.data.data.total
                    this.IsTableLoading = false
                    //this.$message.success("获取成功")
            
                    // #region 获取筛选
                    var init_dialogdata3 = {};
                    for (let [key, value] of Object.entries(response.data.map)) {
                        if(key!=="currentPage" && key!=="PageSize"){
                            init_dialogdata3[key] = value
                        }
                    }
                    this.dialogdata3 = init_dialogdata3
                    // #endregion
            
                    // 初始化Tag
                    this.init_Tags()
                }
            }).catch(error=>{
                this.$message.error(error.data.msg)
                console.log(error)
            })
        },
        // 初始化Tag
        init_Tags(){
            var tags = []
            console.log(this.dialogdata3)
            for (let [key, value] of Object.entries(this.dialogdata3)) {
                console.log(key + ': ' + value);
                if(value!==null){
                    var key_name = key
                    if(key==='name')key_name = "姓名"
                    if(key==='startDate')key_name = "开始日期"
                    if(key==='endDate')key_name = "结束日期"

                    tags.push({ name: `${key_name} : ${value}`, type: 'success' , key:key})
                }
            }
            this.tags = tags
        },
        // 点击查询框确定
        gotosearch(){
            var is_allnull = true
            for (let value of Object.values(this.dialogdata3)) {
                if(value!==null){
                    is_allnull = false
                    break
                }
            }
            if(is_allnull){
                this.$message.error("无筛选参数")
                return
            }
        
            // 初始化Tag
            this.init_Tags()

            // 关闭窗口
            this.dialogVisible3 = false

            // #region 发起条件查询
            this.gettablebycondition()
        },
        // 点击Tag的删除
        removeTag(key){
        // 移除Tag
            console.log(key)
            this.tags = this.tags.filter(tag => tag.key !== key);

            // #region 同时删除查询对象dialogdata3的对应值
            var dialogdata3 =
        {
            "id": null,
            "name":null,
            "startDate":null,
            "endDate":null,
        }
            for (let [key2, value] of Object.entries(this.dialogdata3)) {
                if(key===key2)continue
                dialogdata3[key2] = value
            }
            this.dialogdata3 = dialogdata3

            console.log(this.dialogdata3)
            // #endregion
      

            // 同时发起新条件查询
            this.gettablebycondition()      
        },
        isValidUUID(uuid) {
            const regex = /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i;
            return regex.test(uuid);
        }
    },
    mounted(){
        this.gettablebycondition()
    },
    watch:{
        dialogVisible2:function(){
            if(!this.dialogVisible2){
                if(this.$refs.upload){
                    this.$refs.upload.uploadFiles.splice(0)
                }
            }
        }
    }
}
</script>

<style scoped>
.radio-group-content {
  white-space: normal; /* 允许内容换行 */
  word-break: break-word; /* 在长单词或URL地址内部进行换行 */
}
</style>
