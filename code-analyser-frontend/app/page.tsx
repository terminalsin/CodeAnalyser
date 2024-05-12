import DeployButton from "../components/DeployButton";
import AuthButton from "../components/AuthButton";
import { createClient } from "@/utils/supabase/server";
import ConnectSupabaseSteps from "@/components/tutorial/ConnectSupabaseSteps";
import SignUpUserSteps from "@/components/tutorial/SignUpUserSteps";
import Header from "@/components/Header";
import Link from "next/link";
import HomeButton from "@/components/HomeButton";
import { redirect } from "next/navigation";


export default async function Index() {
  const supabase = createClient();

  const {
    data: { user },
  } = await supabase.auth.getUser();

  if (user) {
    return redirect("/protected");
  }


  return (
    <div className="flex-1 w-full flex flex-col items-center">
      <nav className="w-full flex justify-center border-b border-b-foreground/10 h-16">
        <div className="w-full max-w-full flex justify-between items-center p-3 text-sm">
          <HomeButton />
          {<AuthButton />}
        </div>
      </nav>

      <div className="animate-in flex-1 flex flex-col gap-20 opacity-0 w-full ">
        <Header />
        <main className="flex-1 flex flex-col gap-6 px-80">
        <h2 className="font-bold text-4xl mb-4">In order to use our Code Analyzer, please login before proceeding</h2>
          {/* <h2 className="font-bold text-4xl mb-4">Summarize Code</h2>
          <textarea
        className="w-full h-80 border rounded-md p-2 mb-4 text-black"
        placeholder="Paste your code here..."
      ></textarea>
      <div className="flex flex-1 flex-row">
      <button className="w-full p-3 bg-blue-600 text-black font-bold border-2 border-double rounded-lg hover:bg-blue-500">Summarize</button>
      <select className="block appearance-none w-30 bg-white text-black border border-gray-300 hover:border-gray-500 px-4 py-2 pr-8 rounded leading-tight focus:outline-none focus:bg-white focus:border-gray-500">
        <option value="" disabled selected>Select an option</option>
        <option value="1">1</option>
        <option value="2">2</option>
        <option value="3">3</option>
      </select>
      <Link href="/client">client</Link>
      </div>
          {isSupabaseConnected ? <SignUpUserSteps /> : <ConnectSupabaseSteps />}
      <h2 className="font-bold text-4xl mb-4">Code and Summarization Pairing</h2> */}
      {/* <h4 className="text-2xl"> Please input files </h4> */}
      {/* <div className="flex-1 w-full justify-center items-center">
        <input type="file" 
        className="block w-full text-sm text-slate-500
        file:mr-4 file:py-2 file:px-4
        file:rounded-full file:border-0
        file:text-sm file:font-semibold
        file:bg-blue-50 file:text-blue-700
        hover:file:bg-blue-100
        "/>
      </div> */}
        
        </main>
      
      <footer className="w-full border-t border-t-foreground/10 p-8 flex justify-center text-center text-xs">
        <p>
            Created & Designed by Sonny Zhang & Shanyu Thibaut
        </p>
      </footer>
      </div>
    </div>
  );
}
