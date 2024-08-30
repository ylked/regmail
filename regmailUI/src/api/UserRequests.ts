import {Method, Requests} from "@/api/Requests";
import {LoginRequestDTO} from "@/types/dto/request/LoginRequestDTO";
import {LoginResponseDTO} from "@/types/dto/response/LoginResponseDTO";
import {RefreshRequestDTO} from "@/types/dto/request/RefreshRequestDTO";

export class UserRequests extends Requests {
  protected static instance: UserRequests;

  protected constructor() {
    super();
  }

  public static getInstance(): UserRequests {
    if (!Requests.instance) {
      UserRequests.instance = new UserRequests();
    }
    return UserRequests.instance;
  }

  public async login(dto: LoginRequestDTO): Promise<LoginResponseDTO> {
    return await this.request<LoginRequestDTO, LoginResponseDTO>(
      '/auth/login',
      Method.POST,
      false,
      dto
    );
  }

  public async register(dto: LoginRequestDTO): Promise<LoginResponseDTO> {
    return await this.request<LoginRequestDTO, LoginResponseDTO>(
      '/auth/register',
      Method.POST,
      false,
      dto
    );
  }

  public async refresh(): Promise<LoginResponseDTO> {
    return await this.request<RefreshRequestDTO, LoginResponseDTO>(
      '/auth/refresh',
      Method.POST,
      false,
      {refreshToken: this.store?.user?.refreshToken.token} as RefreshRequestDTO,
    );
  }
}
