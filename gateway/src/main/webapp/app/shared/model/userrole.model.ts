import { IUser } from 'app/shared/model/user.model';

export interface IUserrole {
  id?: number;
  role?: string;
  user?: IUser | null;
}

export const defaultValue: Readonly<IUserrole> = {};
