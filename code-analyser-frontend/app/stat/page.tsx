import StatButton from "@/components/StatButton"
import AuthButton from "@/components/AuthButton"
import HomeButton from "@/components/HomeButton"
import BarGraph from "@/components/BarGraph"
import { redirect } from "next/navigation";
import { createClient } from "@/utils/supabase/server";
import ReactApexChart from "react-apexcharts";
import { createAsyncToken } from "@/utils/supabase/client";
import { Review } from "@/components/NodeDetails";
import AveragePage from "@/components/AveragePage"



export default async function StatPage() {
    const supabase = createClient();

    const {
        data: { user },
    } = await supabase.auth.getUser();

    if (!user) {
        return redirect("/login");
    }
    return (
        <>
            <div className="flex-1 w-full flex flex-col items-center">
                <div className="w-full">
                    <nav className="w-full flex justify-center border-b border-b-foreground/10 h-16">
                        <div className="w-full flex justify-end gap-10 items-center p-3 text-sm">
                            <HomeButton />
                            <StatButton />
                            <AuthButton />
                        </div>
                    </nav>
                </div>
                <div className="flex flex-row w-full mt-10">
                    <div className="flex justify-center rounded-lg flex grow w-1/3 bg-slate-300 m-10">
                        <BarGraph />
                    </div>
                    <div className="rounded-lg flex-1 bg-blue-500 p-20 w-1/2 m-10">
                        <div className="rounded-full bg-blue-800 p-2">
                            <h1 className="text-center text-4xl font-bold">Average Code Score</h1>
                        </div>
                        <div className="flex justify-center">
                            <AveragePage />
                        </div>
                        <div className="flex justify-center mt-3">
                            <div className="w-1/2 p-[4px] bg-black rounded-full my-1" />
                        </div>
                    </div>
                </div>
            </div>

        </>
    )
}