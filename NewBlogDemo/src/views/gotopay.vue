<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div>
    <div v-show="!iframe_show">
      <br><br><br>
      <p v-if="show">正在跳转支付宝支付页，请稍等……</p>
      <p v-else>勿重复跳转，请从订单页重新发起</p>
    </div>
    <iframe v-show="iframe_show" id="paymentIframe" name="paymentIframe" style="width:100%; height:100vh; border:0; display:block;overflow: hidden;"></iframe>

  </div>
</template>

<script>
import axios from '@/utils'

export default {
    components: {
    },
    data() {
        return{
            show:true,
            iframe_show:false
        }
    },
    methods:{
    },
    mounted(){
        this.$store.state.zhezhao_show = false
        // 显不显示跳转
        if(sessionStorage.getItem('StopRedirectPay')==='false'|| sessionStorage.getItem('StopRedirectPay')===null){
            this.show = true
        }else{
            this.show = false
            setTimeout(() => {this.$router.push('home')},500)
        }
    
        // 假设这里是你获取URL参数的代码
        const id = this.$route.query.id;
        const money = this.$route.query.money;
        const num = this.$route.query.num;
        const name = this.$route.query.name;

        if(this.show){
            // 设置延时跳转
            setTimeout(() => {
                // 防止重复跳转
                sessionStorage.setItem('StopRedirectPay',"true")

                // 电脑网页版扫码支付 手机困难
                if(!this.$store.state.IsMobile)window.location.href = `${process.env.VUE_APP_API_URL}test/pay?outTradeNo=${id}&totalAmount=${money}&subject="${name}等${num}件商品"&productCode=FAST_INSTANT_TRADE_PAY`;
                // 手机支付 productCode=QUICK_WAP_WAY
                else {
                    // 内嵌进网页 不然微信内页跳转 太长会被截取 不给跳
                    //window.location.href = `${process.env.VUE_APP_API_URL}test/pay?outTradeNo=${id}&totalAmount=${money}&subject="${name}等${num}件商品"&productCode=QUICK_WAP_WAY`;
                    axios.get(`test/pay?outTradeNo=${id}&totalAmount=${money}&subject="${name}等${num}件商品"&productCode=QUICK_WAP_WAY`).then(response=>{
                        this.iframe_show = true
                        // 表单HTML字符串
                        var formHtml = response.data
                        // 获取 iframe 元素
                        var iframe = document.getElementById('paymentIframe');

                        // 获取 iframe 的文档对象
                        var iframeDoc = iframe.contentDocument || iframe.contentWindow.document;

                        // 将表单 HTML 写入 iframe
                        iframeDoc.open();
                        iframeDoc.write(formHtml);
                        iframeDoc.close();

                        console.log(response)
                    }).catch(error=>{
                        console.log(error)
                    })
                }
      
            }, 500); // 500毫秒后跳转
        }else{
            this.$message("勿重复跳转，请从订单页重新发起")
        }

      
    }
}
</script>

<style scoped>

</style>
