import {defineStore} from "pinia";
import {User} from "@/types/store/User";
import {LoginResponseDTO} from "@/types/dto/response/LoginResponseDTO";
import {UserRequests} from "@/api/UserRequests";

const req = UserRequests.getInstance();

export const useUserStore = defineStore('user', {
    state: () => ({
        user: null as User | null,
      }
    ),
    getters: {
      token: (state) => state.user?.accessToken.token,
    },
    actions: {
      async login(email: string, password: string): Promise<LoginResponseDTO> {
        const r = await req.login({email, password});
        this.user = r;
        return r;
      },
      async register(email: string, password: string) {
        const r = await req.register({email, password});
        this.user = r;
        return r;
      },
      async refresh() {
        const r = await req.refresh();
        this.user = r;
        return r;
      },
      async logout() {
      },
    },
    persist:
      {
        storage: sessionStorage,
      }
    ,
  })
;

export type UserStore = ReturnType<typeof useUserStore>;
