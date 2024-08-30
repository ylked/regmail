<script setup lang="ts">
import {useUserStore} from "@/stores/UserStore";
import {ref} from "vue";
import {useRouter} from "vue-router";

const props = defineProps<{
  login: boolean
}>();

const router = useRouter();

const store = useUserStore();
const username = ref('');
const password = ref('');
const loading = ref(false);

const login_icon = 'mdi-account-key';
const register_icon = 'mdi-account-plus';

const rules = [() => submit()]

async function _login(): Promise<string | boolean> {
  try {
    await store.login(username.value, password.value)
    await router.push('/')
    return true;
  } catch (e) {
    return 'Invalid e-mail or password. Please try again.'
  }
}

async function _register(): Promise<string | boolean> {
  return 'Not implemented';
}

async function submit(): Promise<string | boolean> {
  loading.value = true;
  const r = props.login ? await _login() : await _register();
  loading.value = false;
  return r;
}

function redirect() {
  props.login ? router.push('/register') : router.push('/login');
}

</script>

<template>

  <v-form
    class="d-flex justify-center align-center"
  >
    <v-container fill-height>
      <v-row
        justify="center"
        align="center"
      >
        <v-col cols="12" md="10" lg="8">
          <v-sheet
            elevation="4"
            rounded
            class="ma-2 pa-5"
          >
            <h2
              class="text-h6 mb-5 mx-2"
            >
              {{ login ? 'Login' : 'Register' }}
            </h2>

            <p
              class="text-body-1 mx-2 mb-5"
            >
              {{ login ? 'Sign in to Regmail' : 'Create a new account' }}
            </p>

            <v-text-field
              v-model="username"
              label="E-mail"
              :rules="rules"
              validate-on="submit lazy"
              required
            />
            <v-text-field
              v-model="password"
              label="Password"
              type="password"
              required
            />
            <v-spacer/>

            <v-container>
              <v-row>
                <v-col cols="12" sm="6">
                  <v-btn
                    @click="redirect"
                    :prepend-icon="login? register_icon : login_icon"
                    class="mt-2 mb-2 mx-2"
                    block
                  >
                    {{ login ? 'Register' : 'Login' }}
                  </v-btn>
                </v-col>
                <v-col cols="12" sm="6">
                  <v-btn
                    color="primary"
                    :prepend-icon="login? login_icon : register_icon"
                    class="mt-2 mb-2 mx-2"
                    type="submit"
                    :loading="loading"
                    block
                  >
                    {{ login ? 'Login' : 'Register' }}
                  </v-btn>
                </v-col>

              </v-row>
            </v-container>
          </v-sheet>
        </v-col>
      </v-row>
    </v-container>
  </v-form>
</template>

<style scoped>

</style>
