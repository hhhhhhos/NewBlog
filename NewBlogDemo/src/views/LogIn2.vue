<template>
  <div style="width: 100vw;height: 90vh;display: flex;align-items: center;justify-content: center;">
    <div class="login-container">
      <div class="input-group">
        <div class="input-prefix">+86</div>
        <input type="text" v-model="phone" placeholder="请输入手机号码" />
      </div>
      <button @click="login" class="login-btn">登录</button>
      <div class="options">
        <a href="#" class="forgot-password">忘记密码</a>
        <a href="#" class="register-account">注册账号</a>
      </div>
      <div class="social-login">
        <button class="social-btn wechat">微信登录</button>
        <button class="social-btn apple">苹果登录</button>
      </div>
      <div class="agreement">
        <input type="checkbox" id="agreement" v-model="agreed" />
        <label for="agreement">我同意相关条款（点击查看隐私政策）和《用户协议条款》,未注册的手机号验证后自动创建新账号</label>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      phone: '',
      agreed: false
    };
  },
  methods: {
    login() {
      // 实现登录逻辑
      console.log('手机号: ' + this.phone);
      // 确保用户同意了条款
      if (!this.agreed) {
        alert('请同意用户协议和隐私政策');
        return;
      }
      // TODO: 实现登录逻辑
    }
  }
};
</script>

<style scoped>
.login-container {
  max-width: 300px;
  margin: 50px auto;
  padding: 20px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

.input-group {
  display: flex;
  margin-bottom: 20px;
}

.input-prefix {
  padding: 10px;
  background-color: #f1f1f1;
  color: #333;
}

.input-group input {
  flex: 1;
  padding: 10px;
  border: 1px solid #ddd;
}

.login-btn {
  width: 100%;
  padding: 10px;
  background-color: #ff4d4f;
  border: none;
  color: white;
  margin-bottom: 20px;
}

.options {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.social-login {
  display: flex;
  justify-content: space-around;
  margin-bottom: 20px;
}

.social-btn {
  padding: 10px;
  width: 48%; /* Fallback for older browsers */
  width: calc(50% - 4px); /* Modern browsers */
  border: none;
}

.social-btn.wechat {
  background-color: #09bb07;
  color: white;
}

.social-btn.apple {
  background-color: black;
  color: white;
}

.agreement {
  font-size: 0.75rem;
}
.agreement input[type="checkbox"] {
  margin-right: 5px;
}
</style>




