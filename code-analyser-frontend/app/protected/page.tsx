import DeployButton from "@/components/DeployButton";
import AuthButton from "@/components/AuthButton";
import { createClient } from "@/utils/supabase/server";
import FetchDataSteps from "@/components/tutorial/FetchDataSteps";
import Header from "@/components/Header";
import { redirect } from "next/navigation";
import TestButton from "@/components/TestButton.client";
import NodeList from "@/components/NodeDetails";
import ContentNode from "@/components/Node";
import History from "../history/page";
import StatButton from "@/components/StatButton";
import HomeButton from "@/components/HomeButton";

export default async function ProtectedPage() {
  const supabase = createClient();

  const {
    data: { user },
  } = await supabase.auth.getUser();

  if (!user) {
    return redirect("/login");
  }

  console.log("I render once");

  return (
    <div className="flex-1 w-full flex flex-col gap-10 items-center">
      <div className="w-full">
        {/* <div className="py-6 font-bold bg-purple-950 text-center">
          Admin
        </div> */}
        <nav className="w-full flex justify-center border-b border-b-foreground/10 h-16">
          <div className="w-full flex justify-end gap-10 items-center p-3 text-sm">
            <HomeButton />
            <StatButton/>
            <AuthButton />
          </div>
        </nav>
      </div>

      <div className="animate-in flex-1 flex flex-col gap-20 opacity-0 w-full px-3">
        <Header />
        <main className="flex-1 flex flex-col gap-6 items-center">
          <TestButton />
          <History />
          {/* <ContentNode /> */}
          
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
