import {TokenResponseDTO} from "@/types/dto/response/TokenResponseDTO";
import {UserResponseDTO} from "@/types/dto/response/UserResponseDTO";

export type LoginResponseDTO = {
  accessToken: TokenResponseDTO;
  refreshToken: TokenResponseDTO;
  user: UserResponseDTO;
}
