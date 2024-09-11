<template>
    <div>
      <h1 v-if="false">{{$route.name}}</h1>
        <product :biao="`t_${$route.name}`" :columns="columns[$route.name]" 
        :fliter_list="fliter_list[$route.name]"/>
    </div>
  </template>
  
<script>
import product from './productdata'

export default {
    components:{
        product
    },
    data() {
        return{
            // el-table 渲染的列名
            columns:{
                comment:[
                    { prop: 'id', label: 'ID', width: '80' },
                    { prop: 'user_id', label: '用户ID', width: '90' },
                    { prop: 'product_id', label: '文章ID', width: '90' },
                    { prop: 'father_comm_id', label: '父评论ID', width: '100' },
                    { prop: 'love_list', label: '点赞数' , width: '80' ,align:'center'},
                    { prop: 'ip', label: 'ip' , width: '80' ,align:'center'},
                    { prop: 'ip_location', label: 'ip地点' , width: '100' ,align:'center'},
                    { prop: 'info', label: '评论内容' },
                    { prop: 'replay_to_user_id', label: '回复ID', width: '90' },
                    { prop: 'is_show', label: '显示', width: '80' },
                    { prop: 'is_top', label: '置顶', width: '80' },
                    { prop: 'create_time', label: '创建时间', width: '180' },
                ],
                friend:[
                    { prop: 'id', label: 'ID', width: '80' },
                    { prop: 'user_id', label: '用户ID', width: '90' },
                    { prop: 'name', label: '姓名', width: '120' },
                    { prop: 'info', label: '内容' , width: '120'},
                    { prop: 'blog_url', label: '博客地址', },
                    { prop: 'fan', label: '被关注', width: '90' },
                    { prop: 'invalid', label: '无效', width: '80' },
                    { prop: 'qq_mail', label: 'qq邮箱', width: '120' },
                    { prop: 'avata_url', label: '头像地址' },
                    { prop: 'create_time', label: '创建时间', width: '180' },
                ],
                chat:[
                    { prop: 'id', label: 'ID', width: '80' },
                    { prop: 'user_id', label: '用户ID', width: '90' },
                    { prop: 'receiver_id', label: '接收人ID', width: '100' },
                    { prop: 'group_uuid', label: '聊天组', width: '120' },
                    { prop: 'info', label: '内容' },
                    { prop: 'is_read', label: '已读', width: '80' },
                    { prop: 'create_time', label: '创建时间', width: '180' },
                ],
                fan:[
                    { prop: 'id', label: 'ID' },
                    { prop: 'user_id', label: '用户ID'  },
                    { prop: 'user_id2', label: '被粉用户ID' },
                    { prop: 'create_time', label: '创建时间', width: '180' },
                ],
                kefu_chat_history:[
                    { prop: 'id', label: 'ID', width: '80' },
                    { prop: 'user_id', label: '用户ID', width: '90' },
                    { prop: 'kefu_name', label: '客服名称', width: '100' },
                    { prop: 'send_json', label: '内容' },
                    { prop: 'create_time', label: '创建时间', width: '180' },
                    { prop: 'is_finish', label: '已完结', width: '90' },
                ],
                user:[
                    { prop: 'id', label: 'ID', width: '80' },
                    { prop: 'name', label: '姓名', width: '80' },
                    { prop: 'role', label: '角色', width: '80',align:'center' },
                    { prop: 'wechat_nickname', label: '微信名', width: '80' },
                    { prop: 'wechat_headimgurl', label: '微信头像', width: '100' },
                    //{ prop: 'addresses', label: '地址' },       
                    { prop: 'ip_location', label: 'ip地点', width: '90' },
                    //{ prop: 'money', label: '钱包', width: '100' },
                    { prop: 'last_visited_time', label: '最后访问时间', width: '180' },
                    { prop: 'create_time', label: '创建时间', width: '180' },
                    { prop: 'option', label: '设置', width: '80' },
                    { prop: 'password', label: '密码' , width: '120' },
                    { prop: 'email', label: '邮箱'},
                    { prop: 'blog', label: '博客'},
                    { prop: 'age', label: '年龄', width: '80' },
                    { prop: 'sex', label: '性别', width: '80' },
                    { prop: 'phone', label: '电话', width: '80' },
                ],
                user_agent_details:[
                    { prop: 'id', label: 'ID', width: '80' },
                    { prop: 'create_time', label: '访问时间', width: '160' },
                    { prop: 'visitor_name', label: '用户名', width: '90' },
                    { prop: 'wechat_nickname', label: '微信名', width: '90' },
                    { prop: 'realIp', label: '访问ip', width: '120' },
                    { prop: 'city', label: 'ip地点', width: '120' },
                    { prop: 'originalURI', label: '访问URL' , width: '120'},
                    { prop: 'method', label: '访问方法' , width: '100'},
                    { prop: 'forwardedProto', label: '协议' , width: '80'},
                    { prop: 'user_agent', label: 'user_agent' , width: '220'},
                    { prop: 'deviceClass', label: '设备', width: '80' },
                    { prop: 'deviceBrand', label: '设备品牌', width: '100' },
                    { prop: 'deviceFirmwareVersion', label: '版本', width: '70' },
                    { prop: 'operatingSystemName', label: '操作系统', width: '120' },
                    { prop: 'operatingSystemVersion', label: '版本', width: '70' },
                    { prop: 'agentName', label: '浏览器', width: '90' },
                    { prop: 'agentVersion', label: '版本', width: '80' },
                    { prop: 'user_uuid', label: '访客标识', width: '300' },
                ],
                user_option:[
                    { prop: 'id', label: 'ID', width: '80' },
                    { prop: 'user_id', label: '用户ID' },
                    { prop: 'show_product_scale_win', label: '是否显示缩放调节窗' , width: '120'},
                    { prop: 'subscribe_comment_reply_by_mail', label: '邮件订阅评论回复' , width: '120'},
                    { prop: 'create_time', label: '访问时间', width: '160' },
                ]
                

            },
            // 使用fliter组件的header
            fliter_list:{
                comment:['置顶', '显示', '主页显示','分类','ip地点','用户ID','文章ID','父评论ID','回复ID'],
                friend:['被关注','无效','用户ID'],
                chat:['用户ID','接收人ID','聊天组','已读'],
                kefu_chat_history:['用户ID','客服名称','已完结'],
                user:['ip地点','角色'],
                user_agent_details:['用户名','微信名','ip地点','访问ip','访问URL','访问方法','协议','设备','设备品牌','版本','操作系统','访客标识','浏览器','user_agent'],
                user_option:['用户ID']
            }
        }
    },
    methods:{
    },
    mounted() {

    }
}
</script>
  
<style scoped>

</style>