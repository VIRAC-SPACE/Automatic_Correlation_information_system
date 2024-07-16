import {Role} from './role';

export class User {
  id: number | undefined;
  email: string | undefined;
  password: string | undefined;
  name: string | undefined;
  last_name: string | undefined;
  fullname: string | undefined;
  isNew: boolean | undefined;
  roles: Role[]  | undefined ;
  authdata?: string;
}
