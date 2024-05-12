import { createBrowserClient } from "@supabase/ssr";
import { RxStomp } from "@stomp/rx-stomp";

export const createClient = () =>
  createBrowserClient(
    process.env.NEXT_PUBLIC_SUPABASE_URL!,
    process.env.NEXT_PUBLIC_SUPABASE_ANON_KEY!,
  );

export const createAsyncToken = async () => {
  const supabase = createClient();
  const { data: { session } } = await supabase.auth.getSession();
  return session?.access_token;
} 

export const createStompClient = async () => {
  const rxStomp = new RxStomp();
  const token = await createAsyncToken();

  if (token === undefined) {
    throw new Error('Failed to get token');
  }

  rxStomp.configure({
    brokerURL: 'ws://localhost:8080/ws',
    connectHeaders: {
      'Authorization': `Bearer ${token}`
    },
    
    beforeConnect: () => {
      console.log('Attempting to connect to WebSocket...');
    }
  });

  rxStomp.activate();
  return rxStomp;
}