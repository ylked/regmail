import {UserStore, useUserStore} from "@/stores/UserStore";
import axios from "axios";
import {RequestError} from "@/error/RequestError";

export enum Method {
  GET = 'GET',
  POST = 'POST',
  PUT = 'PUT',
  PATCH = 'PATCH',
  DELETE = 'DELETE',
}
export class Requests {
  private readonly BASE_URL = 'http://localhost:8000/api';
  protected store: UserStore | null = null;

  protected static instance: Requests;

  protected initStore() {
    this.store = useUserStore();
  }

  protected get Store(): UserStore {
    if(!this.store) {
      this.initStore();
    }
    return this.store as UserStore;
  }

  public static getInstance(): Requests {
    if (!Requests.instance) {
      Requests.instance = new Requests();
    }
    return Requests.instance;
  }

  private headers(auth: boolean = true): any {
    const headers: any = {}
    headers['Content-Type'] = 'application/json'
    if(auth) {
      headers['Authorization'] = `Bearer ${this.store?.token}`
    }
    return headers
  }

  protected async request<T, U>(
    path: string,
    method: Method,
    auth: boolean,
    body?: T,
    firstTry = true,
  ): Promise<U> {
    const url = `${this.BASE_URL}${path}`;
    try{
      const response = await axios({
        url: url,
        method: method,
        headers: this.headers(auth),
        data: body,
      })
      return response.data
    } catch (error: any) {
      if(!error.response) {
        throw new Error("network error")
      }
      if(!error.request){
        throw new Error("network error")
      }

      // if the request used a possibly expired token, try to refresh it
      // but only once, to avoid infinite loops
      if(error.response.status === 401 && firstTry && auth) {
        console.log("token possibly expired, trying to refresh it...")

        // try to refresh the token
        await this.Store.refresh()

        // retry the request
        return await this.request(url, method, auth, body, false)
      } else if (error.response.status === 401) {
        throw new RequestError("invalid credentials", 401)
      }
      throw new RequestError(error.response.data.message, error.response.status)
    }
  }
}
